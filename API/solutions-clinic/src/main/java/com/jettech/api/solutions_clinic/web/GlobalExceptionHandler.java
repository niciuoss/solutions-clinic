package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static Map<String, Object> errorBody(String error, String message, int status) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status);
        body.put("error", error);
        body.put("message", message);
        body.put("path", "");
        return body;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorBody("Not Found", ex.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(AppointmentConflictException.class)
    public ResponseEntity<Map<String, Object>> handleAppointmentConflict(AppointmentConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(errorBody("Conflict", ex.getMessage(), HttpStatus.CONFLICT.value()));
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateEntity(DuplicateEntityException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(errorBody("Conflict", ex.getMessage(), HttpStatus.CONFLICT.value()));
    }

    @ExceptionHandler(InvalidStateException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidState(InvalidStateException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(errorBody("Unprocessable Entity", ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY.value()));
    }

    @ExceptionHandler(ScheduleValidationException.class)
    public ResponseEntity<Map<String, Object>> handleScheduleValidation(ScheduleValidationException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(errorBody("Unprocessable Entity", ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY.value()));
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidRequest(InvalidRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorBody("Bad Request", ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorBody("Internal Server Error", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}
