package com.jettech.api.solutions_clinic.exception;

/**
 * Exceção lançada quando uma operação não é permitida no estado atual da entidade
 * (ex.: atualizar agendamento cancelado, procedimento inativo).
 */
public class InvalidStateException extends RuntimeException {

    public InvalidStateException(String message) {
        super(message);
    }

    public InvalidStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
