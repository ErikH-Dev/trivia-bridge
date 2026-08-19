import { apiClient } from "./client"
import type { TriviaCategoriesResponse } from "./types"

export async function getCategories(): Promise<TriviaCategoriesResponse> {
    const response = await apiClient.get<TriviaCategoriesResponse>("/categories")

    return response.data
}
