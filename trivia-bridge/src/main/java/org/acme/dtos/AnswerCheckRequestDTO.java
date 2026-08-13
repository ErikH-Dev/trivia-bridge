package org.acme.dtos;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record AnswerCheckRequestDTO(
    @NotNull
    UUID questionId,
    @NotNull
    UUID answerId
) {}
