package com.jettech.api.solutions_clinic.exception;

/**
 * Exceção lançada quando há conflito de horário em agendamento (profissional ou sala).
 */
public class AppointmentConflictException extends RuntimeException {

    public AppointmentConflictException(String message) {
        super(message);
    }

    public AppointmentConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
