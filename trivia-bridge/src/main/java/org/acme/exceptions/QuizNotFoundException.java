package org.acme.exceptions;

import java.util.UUID;

public class QuizNotFoundException extends RuntimeException {

    public QuizNotFoundException(UUID id) {
        super("Quiz with id " + id + " not found.");
    }
}
