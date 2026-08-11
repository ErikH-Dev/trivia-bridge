package org.acme.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum QuestionType {
    ANY,
    @JsonProperty("multiple")
    MULTIPLE,
    @JsonProperty("boolean")
    BOOLEAN,
}
