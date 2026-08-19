package org.acme.dtos.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CategoriesResponseDTO(
        @JsonProperty("trivia_categories")
        List<CategoryResponseDTO> categories) {
}
