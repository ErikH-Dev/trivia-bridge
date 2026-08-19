package org.acme.exceptions;

public class InvalidQuizSubmissionException extends RuntimeException {

    public InvalidQuizSubmissionException(String message) {
        super(message);
    }
}