package org.acme.dtos.response;

import java.util.List;
import java.util.UUID;

public record QuizResponseDTO(
    UUID id,
    List<QuizQuestionResponseDTO> questions
) {}
