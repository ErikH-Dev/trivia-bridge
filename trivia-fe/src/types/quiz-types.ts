export const QUESTION_DIFFICULTIES = {
    ANY: "ANY",
    EASY: "EASY",
    MEDIUM: "MEDIUM",
    HARD: "HARD",
} as const

export type QuestionDifficultyRequest =
    (typeof QUESTION_DIFFICULTIES)[keyof typeof QUESTION_DIFFICULTIES]

export const QUESTION_TYPES = {
    ANY: "ANY",
    MULTIPLE: "MULTIPLE",
    BOOLEAN: "BOOLEAN",
} as const

export type QuestionTypeRequest = (typeof QUESTION_TYPES)[keyof typeof QUESTION_TYPES]

export const ANY_CATEGORY = { id: 0, name: "Any" } as const

export const QUIZ_DIFFICULTIES = [
    { label: "Any", value: QUESTION_DIFFICULTIES.ANY },
    { label: "Easy", value: QUESTION_DIFFICULTIES.EASY },
    { label: "Medium", value: QUESTION_DIFFICULTIES.MEDIUM },
    { label: "Hard", value: QUESTION_DIFFICULTIES.HARD },
] as const

export const QUIZ_QUESTION_TYPES = [
    { label: "Any", value: QUESTION_TYPES.ANY },
    { label: "Multiple choice", value: QUESTION_TYPES.MULTIPLE },
    { label: "True or false", value: QUESTION_TYPES.BOOLEAN },
] as const
