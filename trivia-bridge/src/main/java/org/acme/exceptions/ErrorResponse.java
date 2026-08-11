package org.acme.exceptions;

public record ErrorResponse(
    String code, 
    String message
) {
}