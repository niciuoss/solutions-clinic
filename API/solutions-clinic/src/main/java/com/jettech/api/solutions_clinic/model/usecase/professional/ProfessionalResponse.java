package com.jettech.api.solutions_clinic.model.usecase.professional;

import com.jettech.api.solutions_clinic.model.entity.DocumentType;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProfessionalResponse(
    UUID id,
    UUID userId,
    UUID tenantId,
    String specialty,
    DocumentType documentType,
    String documentNumber,
    String documentState,
    String bio,
    boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}

