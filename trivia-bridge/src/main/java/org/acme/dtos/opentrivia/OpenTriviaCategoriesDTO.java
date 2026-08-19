package org.acme.dtos.opentrivia;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenTriviaCategoriesDTO(
		@JsonProperty("trivia_categories") 
		List<OpenTriviaCategoryDTO> triviaCategories) {
}
