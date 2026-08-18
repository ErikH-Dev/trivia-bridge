import axios from "axios"

import type { ApiErrorResponse } from "./types"

const DEFAULT_API_BASE_URL = "http://localhost:8080"

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? DEFAULT_API_BASE_URL,
  timeout: 10_000,
  headers: {
    Accept: "application/json",
  },
})

export function getApiErrorMessage(error: unknown): string {
  if (!axios.isAxiosError<ApiErrorResponse>(error)) {
    return error instanceof Error ? error.message : "An unexpected error occurred"
  }

  const apiError = error.response?.data

  return apiError ? `${apiError.code}: ${apiError.message}` : error.message
}
