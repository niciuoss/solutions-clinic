package com.jettech.api.solutions_clinic.exception;

/**
 * Exceção lançada quando uma entidade não é encontrada (ex.: clínica, paciente, profissional, sala).
 */
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String entityType, Object id) {
        super(entityType + " não encontrado(a) com ID: " + id);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
