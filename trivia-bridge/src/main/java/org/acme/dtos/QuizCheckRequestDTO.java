package org.acme.dtos;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record QuizCheckRequestDTO (
    @NotNull
    UUID quizId,

    @NotEmpty
    @Size(max = 50)
    List<@NotNull @Valid AnswerCheckRequestDTO> answers
) {}
