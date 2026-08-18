import { apiClient } from "./client"
import type { QuestionsRequest, QuizCheckRequest, QuizCheckResponse, QuizResponse } from "./types"

export async function createQuiz(params: QuestionsRequest): Promise<QuizResponse> {
    const response = await apiClient.get<QuizResponse>("/questions", { params })

    return response.data
}

export async function submitQuizAnswers(request: QuizCheckRequest): Promise<QuizCheckResponse> {
    const response = await apiClient.post<QuizCheckResponse>("/checkanswers", request)

    return response.data
}
