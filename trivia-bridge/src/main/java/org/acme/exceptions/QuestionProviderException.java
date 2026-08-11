package org.acme.exceptions;

public class QuestionProviderException extends RuntimeException {
    public QuestionProviderException() {
        super("An error occurred while fetching questions from the question provider.");
    }

    public QuestionProviderException(String message) {
        super(message);
    }

    public QuestionProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
