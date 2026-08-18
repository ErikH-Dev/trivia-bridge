import type { QuestionDifficulty } from "@/api/types"
import { Badge } from "@/components/ui/badge"

const DIFFICULTY_PRESENTATION = {
    easy: {
        label: "Easy",
        className: "bg-green-50 text-green-700 bg-green-950 text-green-300",
    },
    medium: {
        label: "Medium",
        className: "bg-yellow-50 text-yellow-700 bg-yellow-950 text-yellow-300",
    },
    hard: {
        label: "Hard",
        className: "bg-red-50 text-red-700 bg-red-950 text-red-300",
    },
} satisfies Record<
    QuestionDifficulty,
    {
        label: string
        className: string
    }
>

interface DifficultyBadgeProps {
    difficulty: QuestionDifficulty
}

export default function DifficultyBadge({ difficulty }: Readonly<DifficultyBadgeProps>) {
    const presentation = DIFFICULTY_PRESENTATION[difficulty]

    return (
        <Badge variant="secondary" className={presentation.className}>
            {presentation.label}
        </Badge>
    )
}
