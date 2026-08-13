package org.acme.dtos.response;

import java.util.UUID;

public record QuestionCheckResponseDTO(
    UUID questionId,
    UUID selectedAnswerId,
    UUID correctAnswerId,
    boolean correct
) {
    
}
