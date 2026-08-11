package org.acme.exceptions;

import java.util.UUID;

public class AnswerNotFoundException extends RuntimeException {

    public AnswerNotFoundException(UUID id) {
        super("Answer with id " + id + " not found.");
    }
}
