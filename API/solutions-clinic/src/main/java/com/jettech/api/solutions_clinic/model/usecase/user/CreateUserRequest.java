package com.jettech.api.solutions_clinic.model.usecase.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(
    @NotBlank(message = "O campo [firstName] é obrigatório")
    String firstName,
    
    @NotBlank(message = "O campo [lastName] é obrigatório")
    String lastName,
    
    @NotBlank(message = "O campo [email] é obrigatório")
    @Email(message = "O campo [email] deve ser um email válido")
    String email,
    
    @NotBlank(message = "O campo [password] é obrigatório")
    String password
) {
}
