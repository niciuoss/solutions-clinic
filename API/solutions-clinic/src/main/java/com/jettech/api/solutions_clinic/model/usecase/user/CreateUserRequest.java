package com.jettech.api.solutions_clinic.model.usecase.user;

public record CreateUserRequest(String name, String email, String password) {
}
