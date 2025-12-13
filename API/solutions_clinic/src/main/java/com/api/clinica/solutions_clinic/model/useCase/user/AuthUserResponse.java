package com.api.clinica.solutions_clinic.model.useCase.user;

public record AuthUserResponse(String access_token, Long expires_in) {
}
