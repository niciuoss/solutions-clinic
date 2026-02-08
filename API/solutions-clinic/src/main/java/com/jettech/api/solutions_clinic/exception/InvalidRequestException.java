package com.jettech.api.solutions_clinic.exception;

/**
 * Exceção lançada quando a requisição ou parâmetros violam regras de negócio
 * (ex.: papel inválido, tipo de categoria incompatível, plano não suportado).
 */
public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }

    public InvalidRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
