package com.jettech.api.solutions_clinic.exception;

import java.security.GeneralSecurityException;

/**
 * Exceção de falha de autenticação/autorização.
 * Estende GeneralSecurityException em vez de javax.naming.AuthenticationException (JNDI).
 */
public class AuthenticationFailedException extends GeneralSecurityException {

    public AuthenticationFailedException() {
        super();
    }

    public AuthenticationFailedException(String message) {
        super(message);
    }

    public AuthenticationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticationFailedException(Throwable cause) {
        super(cause);
    }
}
