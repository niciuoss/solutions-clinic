package com.jettech.api.solutions_clinic.exception;

/**
 * Exceção lançada quando já existe uma entidade com os mesmos dados (ex.: paciente com CPF, profissional duplicado).
 */
public class DuplicateEntityException extends RuntimeException {

    public DuplicateEntityException(String message) {
        super(message);
    }

    public DuplicateEntityException(String message, Throwable cause) {
        super(message, cause);
    }
}
