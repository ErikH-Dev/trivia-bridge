package org.acme.dtos.opentrivia;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record OpenTriviaQuizDTO(
    @JsonProperty("response_code")
    int responseCode,

    @NotEmpty
    List<@Valid OpenTriviaQuestionDTO> results
) {

}
