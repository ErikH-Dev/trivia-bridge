package org.acme.dtos.response;

import java.util.List;
import java.util.UUID;

import org.acme.enums.QuestionDifficulty;
import org.acme.enums.QuestionType;

public record QuizQuestionResponseDTO(
    UUID id,
    String category,
    QuestionDifficulty difficulty,
    QuestionType type,
    String question,
    List<QuizAnswerResponseDTO> options
) {}
