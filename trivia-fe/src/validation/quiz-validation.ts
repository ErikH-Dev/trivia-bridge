import { z } from "zod"

import type { AnswerCheckRequest, QuestionsRequest, QuizResponse } from "@/api/types"
import { QUESTION_DIFFICULTIES, QUESTION_TYPES } from "@/types/quiz-types"

export type ValidationResult<T> =
    | {
          success: true
          data: T
      }
    | {
          success: false
          message: string
      }

const quizSettingsSchema: z.ZodType<QuestionsRequest> = z.object({
    amount: z.coerce
        .number()
        .int("Amount must be a whole number")
        .min(1, "Amount must be at least 1")
        .max(50, "Amount cannot exceed 50"),
    category: z.coerce
        .number()
        .int("Category must be a whole number")
        .nonnegative("Category cannot be negative"),
    difficulty: z.enum(QUESTION_DIFFICULTIES),
    type: z.enum(QUESTION_TYPES),
})

export function parseQuizSettings(formData: FormData): ValidationResult<QuestionsRequest> {
    const result = quizSettingsSchema.safeParse({
        amount: formData.get("amount"),
        category: formData.get("category"),
        difficulty: formData.get("difficulty"),
        type: formData.get("type"),
    })

    if (!result.success) {
        return {
            success: false,
            message: result.error.issues[0]?.message ?? "The quiz settings are invalid.",
        }
    }

    return {
        success: true,
        data: result.data,
    }
}

export function parseQuizAnswers(
    formData: FormData,
    quiz: QuizResponse
): ValidationResult<AnswerCheckRequest[]> {
    const answers: AnswerCheckRequest[] = []

    for (const question of quiz.questions) {
        const submittedValues = formData.getAll(question.id)
        const answerId = submittedValues[0]

        if (submittedValues.length !== 1 || typeof answerId !== "string") {
            return {
                success: false,
                message: "Please answer every question.",
            }
        }

        const belongsToQuestion = question.options.some((option) => option.id === answerId)

        if (!belongsToQuestion) {
            return {
                success: false,
                message: "One of the submitted answers is invalid.",
            }
        }

        answers.push({
            questionId: question.id,
            answerId,
        })
    }

    return {
        success: true,
        data: answers,
    }
}
