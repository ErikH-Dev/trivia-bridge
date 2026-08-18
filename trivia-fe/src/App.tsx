import { useState } from "react"

import type { QuizCheckResponse, QuizResponse } from "./api/types"
import QuizCreationCard from "./components/quiz-creation-card"
import QuizResults from "./components/quiz-results"
import QuestionsContainer from "./components/questions-container"

export default function App() {
    const [quiz, setQuiz] = useState<QuizResponse | null>(null)
    const [result, setResult] = useState<QuizCheckResponse | null>(null)

    function handleQuizCreated(createdQuiz: QuizResponse) {
        setQuiz(createdQuiz)
        setResult(null)
    }

    return (
        <main className="mx-auto min-h-svh w-full max-w-7xl space-y-4 px-4 py-8">
            <QuizCreationCard onQuizCreated={handleQuizCreated} />
            {quiz && !result && <QuestionsContainer quiz={quiz} onQuizChecked={setResult} />}
            {quiz && result && <QuizResults quiz={quiz} result={result} />}
        </main>
    )
}
