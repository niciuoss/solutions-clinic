package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    // --- Resposta padronizada ---

    private static Map<String, Object> errorBody(String error, String message, int status) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status);
        body.put("error", error);
        body.put("message", message != null && !message.isBlank() ? message : "Erro não especificado");
        return body;
    }

    private static String messageOrDefault(String exceptionMessage, String defaultMessage) {
        return (exceptionMessage != null && !exceptionMessage.isBlank()) ? exceptionMessage : defaultMessage;
    }

    // --- 400 Bad Request ---

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidRequest(InvalidRequestException ex) {
        String message = messageOrDefault(ex.getMessage(), ApiError.INVALID_REQUEST.getDefaultMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorBody(ApiError.INVALID_REQUEST.getError(), message, HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + (fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "inválido"))
                .collect(Collectors.joining("; "));
        String message = messageOrDefault(details, ApiError.VALIDATION_FAILED.getDefaultMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorBody(ApiError.VALIDATION_FAILED.getError(), message, HttpStatus.BAD_REQUEST.value()));
    }

    // --- 401 Unauthorized ---

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationFailed(AuthenticationFailedException ex) {
        String message = messageOrDefault(ex.getMessage(), ApiError.AUTHENTICATION_FAILED.getDefaultMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(errorBody(ApiError.AUTHENTICATION_FAILED.getError(), message, HttpStatus.UNAUTHORIZED.value()));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameNotFound(UsernameNotFoundException ex) {
        String message = messageOrDefault(ex.getMessage(), ApiError.AUTHENTICATION_FAILED.getDefaultMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(errorBody(ApiError.AUTHENTICATION_FAILED.getError(), message, HttpStatus.UNAUTHORIZED.value()));
    }

    // --- 404 Not Found ---

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFound(EntityNotFoundException ex) {
        String message = messageOrDefault(ex.getMessage(), ApiError.NOT_FOUND.getDefaultMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorBody(ApiError.NOT_FOUND.getError(), message, HttpStatus.NOT_FOUND.value()));
    }

    // --- 409 Conflict ---

    @ExceptionHandler(AppointmentConflictException.class)
    public ResponseEntity<Map<String, Object>> handleAppointmentConflict(AppointmentConflictException ex) {
        String message = messageOrDefault(ex.getMessage(), ApiError.APPOINTMENT_CONFLICT.getDefaultMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(errorBody(ApiError.APPOINTMENT_CONFLICT.getError(), message, HttpStatus.CONFLICT.value()));
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateEntity(DuplicateEntityException ex) {
        String message = messageOrDefault(ex.getMessage(), ApiError.DUPLICATE_ENTITY.getDefaultMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(errorBody(ApiError.DUPLICATE_ENTITY.getError(), message, HttpStatus.CONFLICT.value()));
    }

    // --- 422 Unprocessable Entity ---

    @ExceptionHandler(InvalidStateException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidState(InvalidStateException ex) {
        String message = messageOrDefault(ex.getMessage(), ApiError.INVALID_STATE.getDefaultMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(errorBody(ApiError.INVALID_STATE.getError(), message, HttpStatus.UNPROCESSABLE_ENTITY.value()));
    }

    @ExceptionHandler(ScheduleValidationException.class)
    public ResponseEntity<Map<String, Object>> handleScheduleValidation(ScheduleValidationException ex) {
        String message = messageOrDefault(ex.getMessage(), ApiError.SCHEDULE_VALIDATION.getDefaultMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(errorBody(ApiError.SCHEDULE_VALIDATION.getError(), message, HttpStatus.UNPROCESSABLE_ENTITY.value()));
    }

    // --- 500 Internal Server Error (fallback) ---

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        String message = ApiError.INTERNAL_SERVER_ERROR.getDefaultMessage();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorBody(ApiError.INTERNAL_SERVER_ERROR.getError(), message, HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    /**
     * Mapeamento: tipo de exceção → status HTTP e mensagem padronizada.
     */
    private enum ApiError {
        INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Bad Request", "Requisição inválida."),
        VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "Bad Request", "Erro de validação nos dados enviados."),
        AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "Unauthorized", "Falha de autenticação."),
        NOT_FOUND(HttpStatus.NOT_FOUND, "Not Found", "Recurso não encontrado."),
        APPOINTMENT_CONFLICT(HttpStatus.CONFLICT, "Conflict", "Conflito de agendamento."),
        DUPLICATE_ENTITY(HttpStatus.CONFLICT, "Conflict", "Registro duplicado."),
        INVALID_STATE(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", "Estado inválido para esta operação."),
        SCHEDULE_VALIDATION(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", "Horário ou agenda inválida."),
        INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Erro interno do servidor.");

        private final HttpStatus status;
        private final String error;
        private final String defaultMessage;

        ApiError(HttpStatus status, String error, String defaultMessage) {
            this.status = status;
            this.error = error;
            this.defaultMessage = defaultMessage;
        }

        HttpStatus getStatus() { return status; }
        String getError() { return error; }
        String getDefaultMessage() { return defaultMessage; }
    }
}
