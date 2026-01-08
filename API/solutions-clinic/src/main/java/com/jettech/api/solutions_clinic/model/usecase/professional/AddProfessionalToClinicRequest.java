package com.jettech.api.solutions_clinic.model.usecase.professional;

import com.jettech.api.solutions_clinic.model.entity.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddProfessionalToClinicRequest(
    @NotNull(message = "O campo [userId] é obrigatório")
    UUID userId,
    
    @NotNull(message = "O campo [tenantId] é obrigatório")
    UUID tenantId,
    
    @NotBlank(message = "O campo [specialty] é obrigatório")
    String specialty,
    
    @NotNull(message = "O campo [documentType] é obrigatório")
    DocumentType documentType,
    
    @NotBlank(message = "O campo [documentNumber] é obrigatório")
    String documentNumber,
    
    String documentState,
    
    String bio
) {
}

