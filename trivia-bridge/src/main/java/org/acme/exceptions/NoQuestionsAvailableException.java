package org.acme.exceptions;

public class NoQuestionsAvailableException extends RuntimeException {
    public NoQuestionsAvailableException() {
        super("No questions available for the requested parameters.");
    }
}
