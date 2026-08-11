package org.acme.dtos;

import org.acme.enums.QuestionDifficulty;
import org.acme.enums.QuestionType;
import org.jboss.resteasy.reactive.RestQuery;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record QuestionsRequestDTO(
    @RestQuery 
    @Min(value = 1, message = "Amount must be at least 1")
    @Max(value = 50, message = "Amount cannot exceed 50")
    int amount,

    @RestQuery 
    @PositiveOrZero(message = "Category can not be negative")
    int category,

    @RestQuery 
    @NotNull(message = "Difficulty is required")
    QuestionDifficulty difficulty,

    @RestQuery 
    @NotNull(message = "Type is required")
    QuestionType type
) {}
