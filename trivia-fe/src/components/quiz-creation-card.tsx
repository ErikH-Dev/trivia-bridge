import { useActionState, useEffect, useState } from "react"

import {
    ANY_CATEGORY,
    QUESTION_DIFFICULTIES,
    QUESTION_TYPES,
    QUIZ_DIFFICULTIES,
    QUIZ_QUESTION_TYPES,
} from "@/types/quiz-types"
import { parseQuizSettings } from "@/validation/quiz-validation"
import { getApiErrorMessage } from "../api/client"
import { getCategories } from "../api/categories"
import { createQuiz } from "../api/questions"
import type { QuizResponse, TriviaCategory } from "../api/types"
import { Button } from "./ui/button"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "./ui/card"
import { Combobox, ComboboxContent, ComboboxInput, ComboboxItem, ComboboxList } from "./ui/combobox"
import { Field, FieldContent, FieldDescription, FieldLabel, FieldTitle } from "./ui/field"
import { Input } from "./ui/input"
import { RadioGroup, RadioGroupItem } from "./ui/radio-group"
import { toast } from "sonner"

interface QuizCreationCardProps {
    onQuizCreated: (quiz: QuizResponse) => void
}

export default function QuizCreationCard({ onQuizCreated }: Readonly<QuizCreationCardProps>) {
    const [, createQuizAction, isSubmitting] = useActionState(handleCreateQuiz, null)
    const [categories, setCategories] = useState<TriviaCategory[]>([ANY_CATEGORY])

    useEffect(() => {
        let isActive = true

        void getCategories()
            .then(({ trivia_categories }) => {
                if (isActive) {
                    setCategories([ANY_CATEGORY, ...trivia_categories])
                }
            })
            .catch((error) => {
                if (isActive) {
                    toast.error("Could not load categories", {
                        description: getApiErrorMessage(error),
                    })
                }
            })

        return () => {
            isActive = false
        }
    }, [])

    async function handleCreateQuiz(_previousState: null, formData: FormData): Promise<null> {
        const settings = parseQuizSettings(formData)

        if (!settings.success) {
            toast.error("Invalid quiz settings", {
                description: settings.message,
            })

            return null
        }

        try {
            const quiz = await createQuiz(settings.data)
            onQuizCreated(quiz)
        } catch (error) {
            toast.error("Could not generate quiz", {
                description: getApiErrorMessage(error),
            })
        }

        return null
    }

    return (
        <form className="w-full" action={createQuizAction}>
            <Card className="w-full">
                <CardHeader>
                    <CardTitle>Quiz Settings</CardTitle>
                    <CardDescription>Modify the quiz settings below</CardDescription>
                </CardHeader>
                <CardContent className="grid grid-cols-2 gap-4">
                    <Field>
                        <FieldLabel htmlFor="question-amount">Number of questions</FieldLabel>
                        <Input
                            id="question-amount"
                            name="amount"
                            defaultValue="10"
                            type="number"
                            min={1}
                            max={50}
                            required
                            placeholder="Number of questions"
                        />
                        <FieldDescription>Choose between 1 and 50 questions.</FieldDescription>
                    </Field>
                    <Field>
                        <FieldLabel htmlFor="question-category">Category</FieldLabel>
                        <Combobox
                            name="category"
                            required
                            items={categories}
                            defaultValue={ANY_CATEGORY}
                            itemToStringLabel={(item) => item.name}
                            itemToStringValue={(item) => String(item.id)}
                        >
                            <ComboboxInput
                                id="question-category"
                                placeholder="Select a category"
                            />
                            <ComboboxContent>
                                <ComboboxList>
                                    {(item) => (
                                        <ComboboxItem key={item.id} value={item}>
                                            {item.name}
                                        </ComboboxItem>
                                    )}
                                </ComboboxList>
                            </ComboboxContent>
                        </Combobox>
                    </Field>
                    <Field>
                        <FieldLabel>Difficulty</FieldLabel>
                        <RadioGroup
                            name="difficulty"
                            defaultValue={QUESTION_DIFFICULTIES.ANY}
                            className="flex"
                        >
                            {QUIZ_DIFFICULTIES.map(({ label, value }) => (
                                <FieldLabel
                                    key={value}
                                    htmlFor={`difficulty-${value.toLowerCase()}`}
                                >
                                    <Field orientation="horizontal">
                                        <FieldContent>
                                            <FieldTitle>{label}</FieldTitle>
                                        </FieldContent>
                                        <RadioGroupItem
                                            value={value}
                                            id={`difficulty-${value.toLowerCase()}`}
                                        />
                                    </Field>
                                </FieldLabel>
                            ))}
                        </RadioGroup>
                    </Field>
                    <Field>
                        <FieldLabel>Question type</FieldLabel>
                        <RadioGroup name="type" defaultValue={QUESTION_TYPES.ANY} className="flex">
                            {QUIZ_QUESTION_TYPES.map(({ label, value }) => (
                                <FieldLabel
                                    key={value}
                                    htmlFor={`question-type-${value.toLowerCase()}`}
                                >
                                    <Field orientation="horizontal">
                                        <FieldContent>
                                            <FieldTitle>{label}</FieldTitle>
                                        </FieldContent>
                                        <RadioGroupItem
                                            value={value}
                                            id={`question-type-${value.toLowerCase()}`}
                                        />
                                    </Field>
                                </FieldLabel>
                            ))}
                        </RadioGroup>
                    </Field>
                </CardContent>
                <CardFooter>
                    <Button type="submit" disabled={isSubmitting}>
                        {isSubmitting ? "Generating..." : "Generate Quiz"}
                    </Button>
                </CardFooter>
            </Card>
        </form>
    )
}
