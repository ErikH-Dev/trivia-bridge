import { useActionState } from "react"

import { getApiErrorMessage } from "@/api/client"
import { submitQuizAnswers } from "@/api/questions"
import type { QuizCheckResponse, QuizResponse } from "@/api/types"
import { Card, CardContent } from "./ui/card"
import {
    Questionnaire,
    QuestionnaireActions,
    QuestionnaireChoice,
    QuestionnaireChoices,
    QuestionnaireError,
    QuestionnaireItem,
    QuestionnaireNext,
    QuestionnairePrevious,
    QuestionnaireProgress,
    QuestionnaireSubmit,
    QuestionnaireTitle,
} from "./ui/questionnaire"
import { toast } from "sonner"
import { parseQuizAnswers } from "@/validation/quiz-validation"
import DifficultyBadge from "./difficulty-badge"

interface QuestionsContainerProps {
    quiz: QuizResponse
    onQuizChecked: (result: QuizCheckResponse) => void
}

export default function QuestionsContainer({
    quiz,
    onQuizChecked,
}: Readonly<QuestionsContainerProps>) {
    const [, submitAnswersAction, isSubmitting] = useActionState(submitAnswers, null)
    const items = quiz.questions.map((question) => ({
        name: question.id,
        required: true,
        choices: question.options.map((option) => ({ value: option.id })),
    }))

    async function submitAnswers(_previousState: null, formData: FormData): Promise<null> {
        const answers = parseQuizAnswers(formData, quiz)

        if (!answers.success) {
            toast.error("Could not submit answers", {
                description: answers.message,
            })

            return null
        }

        try {
            const result = await submitQuizAnswers({
                quizId: quiz.id,
                answers: answers.data,
            })

            onQuizChecked(result)
        } catch (error) {
            toast.error("Could not check answers", {
                description: getApiErrorMessage(error),
            })
        }

        return null
    }

    return (
        <Card className="w-full">
            <CardContent>
                <Questionnaire items={items} action={submitAnswersAction}>
                    <QuestionnaireProgress />
                    {quiz.questions.map((question) => (
                        <QuestionnaireItem key={question.id} name={question.id} required>
                            <QuestionnaireTitle className="flex flex-col gap-2">
                                {" "}
                                <DifficultyBadge
                                    key={question.id}
                                    difficulty={question.difficulty}
                                />
                                {question.question}
                            </QuestionnaireTitle>
                            <QuestionnaireChoices>
                                {question.options.map((option) => (
                                    <QuestionnaireChoice key={option.id} value={option.id}>
                                        {option.text}
                                    </QuestionnaireChoice>
                                ))}
                            </QuestionnaireChoices>
                            <QuestionnaireError />
                        </QuestionnaireItem>
                    ))}
                    <QuestionnaireActions>
                        <QuestionnairePrevious />
                        <QuestionnaireNext />
                        <QuestionnaireSubmit disabled={isSubmitting}>
                            {isSubmitting ? "Submitting..." : "Submit"}
                        </QuestionnaireSubmit>
                    </QuestionnaireActions>
                </Questionnaire>
            </CardContent>
        </Card>
    )
}
