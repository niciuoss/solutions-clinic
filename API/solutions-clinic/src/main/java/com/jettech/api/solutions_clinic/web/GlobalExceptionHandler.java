package com.jettech.api.solutions_clinic.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", ex.getMessage());
        body.put("path", "");

        // Verificar se a mensagem indica conflito de agendamento
        String message = ex.getMessage();
        if (message != null && (
                message.contains("Já existe um agendamento") ||
                message.contains("conflito") ||
                message.contains("Deseja agendar mesmo assim")
        )) {
            body.put("status", HttpStatus.CONFLICT.value());
            body.put("error", "Conflict");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
