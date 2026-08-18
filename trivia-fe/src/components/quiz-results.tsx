import { useState } from "react"
import { CircleCheckIcon, CircleXIcon } from "lucide-react"

import type { QuizCheckResponse, QuizResponse } from "@/api/types"
import { Badge } from "./ui/badge"
import { Button } from "./ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "./ui/card"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "./ui/table"

const PAGE_SIZE = 10

interface QuizResultsProps {
    quiz: QuizResponse
    result: QuizCheckResponse
}

function ResultBadge({ correct }: Readonly<{ correct: boolean }>) {
    return correct ? (
        <Badge
            variant="secondary"
            className="bg-green-50 text-green-700 dark:bg-green-950 dark:text-green-300 [&>svg]:size-4!"
        >
            <CircleCheckIcon aria-hidden="true" />
        </Badge>
    ) : (
        <Badge variant="destructive" className="[&>svg]:size-4!">
            <CircleXIcon aria-hidden="true" />
        </Badge>
    )
}

export default function QuizResults({ quiz, result }: Readonly<QuizResultsProps>) {
    const [page, setPage] = useState(0)

    const resultsByQuestionId = new Map(
        result.questionResults.map((questionResult) => [questionResult.questionId, questionResult])
    )

    const rows = quiz.questions.flatMap((question, index) => {
        const questionResult = resultsByQuestionId.get(question.id)

        if (!questionResult) return []

        const selectedAnswer = question.options.find(
            ({ id }) => id === questionResult.selectedAnswerId
        )
        const correctAnswer = question.options.find(
            ({ id }) => id === questionResult.correctAnswerId
        )

        return [
            {
                number: index + 1,
                correct: questionResult.correct,
                question: question.question,
                selectedAnswer: selectedAnswer?.text ?? "Unavailable",
                correctAnswer: correctAnswer?.text ?? "Unavailable",
            },
        ]
    })

    const pageCount = Math.max(1, Math.ceil(rows.length / PAGE_SIZE))
    const visibleRows = rows.slice(page * PAGE_SIZE, (page + 1) * PAGE_SIZE)

    return (
        <Card className="w-full">
            <CardHeader>
                <CardTitle>
                    Score: {result.correctAnswerCount} / {result.totalQuestions}
                </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead className="w-12">#</TableHead>
                            <TableHead>Result</TableHead>
                            <TableHead>Question</TableHead>
                            <TableHead>Your answer</TableHead>
                            <TableHead>Correct answer</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {visibleRows.map((row) => (
                            <TableRow key={row.number}>
                                <TableCell>{row.number}</TableCell>
                                <TableCell>
                                    <ResultBadge correct={row.correct} />
                                </TableCell>
                                <TableCell className="min-w-80 whitespace-normal">
                                    {row.question}
                                </TableCell>
                                <TableCell className="min-w-48 whitespace-normal">
                                    {row.selectedAnswer}
                                </TableCell>
                                <TableCell className="min-w-48 whitespace-normal">
                                    {row.correctAnswer}
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>

                {pageCount > 1 && (
                    <div className="flex items-center justify-between">
                        <p className="text-sm text-muted-foreground">
                            Page {page + 1} of {pageCount}
                        </p>
                        <div className="flex gap-2">
                            <Button
                                type="button"
                                variant="outline"
                                disabled={page === 0}
                                onClick={() => setPage((currentPage) => currentPage - 1)}
                            >
                                Previous
                            </Button>
                            <Button
                                type="button"
                                variant="outline"
                                disabled={page === pageCount - 1}
                                onClick={() => setPage((currentPage) => currentPage + 1)}
                            >
                                Next
                            </Button>
                        </div>
                    </div>
                )}
            </CardContent>
        </Card>
    )
}
