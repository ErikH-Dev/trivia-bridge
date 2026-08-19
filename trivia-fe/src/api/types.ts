import type { QuestionDifficultyRequest, QuestionTypeRequest } from "@/types/quiz-types"

export type { QuestionDifficultyRequest, QuestionTypeRequest } from "@/types/quiz-types"

export interface QuestionsRequest {
    amount: number
    category: number
    difficulty: QuestionDifficultyRequest
    type: QuestionTypeRequest
}

export interface TriviaCategory {
    id: number
    name: string
}

export interface TriviaCategoriesResponse {
    trivia_categories: TriviaCategory[]
}

export type QuestionDifficulty = "easy" | "medium" | "hard"

export type QuestionType = "multiple" | "boolean"

export interface QuizAnswer {
    id: string
    text: string
}

export interface QuizQuestion {
    id: string
    category: string
    difficulty: QuestionDifficulty
    type: QuestionType
    question: string
    options: QuizAnswer[]
}

export interface QuizResponse {
    id: string
    questions: QuizQuestion[]
}

export interface AnswerCheckRequest {
    questionId: string
    answerId: string
}

export interface QuizCheckRequest {
    quizId: string
    answers: AnswerCheckRequest[]
}

export interface QuestionCheckResult {
    questionId: string
    selectedAnswerId: string
    correctAnswerId: string
    correct: boolean
}

export interface QuizCheckResponse {
    quizId: string
    correctAnswerCount: number
    totalQuestions: number
    questionResults: QuestionCheckResult[]
}

export interface ApiErrorResponse {
    code: string
    message: string
}
