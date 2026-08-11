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
) {}