package org.acme.exceptions;

public class CategoryProviderException extends RuntimeException {

    public CategoryProviderException(String message) {
        super(message);
    }

    public CategoryProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
