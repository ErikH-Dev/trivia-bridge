package org.acme.dtos.opentrivia;

import java.util.List;

import org.acme.enums.QuestionDifficulty;
import org.acme.enums.QuestionType;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record OpenTriviaQuestionDTO(
    @NotNull 
    QuestionType type,
    
    @NotNull 
    QuestionDifficulty difficulty,
    
    @NotBlank 
    String category,
    
    @NotBlank String question,
    
    @JsonProperty("correct_answer")
    @NotBlank
    String correctAnswer,

    @JsonProperty("incorrect_answers")
    @NotEmpty
    List<@NotBlank String> incorrectAnswers
) {}