package org.acme.dtos;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record QuizCheckRequestDTO (
    @NotNull
    UUID quizId,

    @NotEmpty
    List<@NotNull @Valid AnswerCheckRequestDTO> answers
) {}
