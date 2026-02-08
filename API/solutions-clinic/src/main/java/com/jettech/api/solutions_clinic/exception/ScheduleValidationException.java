package com.jettech.api.solutions_clinic.exception;

/**
 * Exceção lançada quando a validação de agenda falha (horário fora do expediente,
 * intervalo de almoço, duração inválida, etc.).
 */
public class ScheduleValidationException extends RuntimeException {

    public ScheduleValidationException(String message) {
        super(message);
    }

    public ScheduleValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
