package org.acme.dtos.response;

import java.util.UUID;

public record QuizAnswerResponseDTO(
    UUID id,
    String text
) {}
    