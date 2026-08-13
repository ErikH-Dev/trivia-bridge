package org.acme.entities;

import java.util.List;
import java.util.UUID;

import org.acme.enums.QuestionDifficulty;
import org.acme.enums.QuestionType;

public record Question(
    UUID id,
    QuestionType type,
    QuestionDifficulty difficulty,
    String category,
    String question,
    List<Answer> incorrectAnswers,
    Answer correctAnswer
) {
    public boolean containsAnswer(UUID answerId) {
        return correctAnswer.id().equals(answerId) || incorrectAnswers.stream().anyMatch(a -> a.id().equals(answerId));
    }

    public boolean isCorrect(UUID answerId) {
        return correctAnswer.id().equals(answerId);
    }
}