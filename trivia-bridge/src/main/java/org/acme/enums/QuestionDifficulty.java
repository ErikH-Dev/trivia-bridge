package org.acme.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum QuestionDifficulty {
    ANY,
    @JsonProperty("easy")
    EASY,
    @JsonProperty("medium")
    MEDIUM,
    @JsonProperty("hard")
    HARD,
}
