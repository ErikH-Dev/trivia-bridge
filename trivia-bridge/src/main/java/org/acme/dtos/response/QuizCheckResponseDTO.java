package org.acme.dtos.response;

import java.util.List;
import java.util.UUID;

public record QuizCheckResponseDTO(
    UUID quizId,
    int correctAnswerCount,
    int totalQuestions,
    List<QuestionCheckResponseDTO> questionResults
) {    
}
