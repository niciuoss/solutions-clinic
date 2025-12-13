package com.api.clinica.solutions_clinic.exception;

public class JWTVerificationException extends RuntimeException {
    public JWTVerificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
