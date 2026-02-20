package com.jettech.api.solutions_clinic.model.usecase.medicalrecordtemplate;

import com.fasterxml.jackson.annotation.JsonRawValue;

import java.time.LocalDateTime;
import java.util.UUID;

public record MedicalRecordTemplateResponse(
    UUID id,
    UUID tenantId,       // null = template global (sistema)
    String name,
    String professionalType,
    @JsonRawValue String schema,
    boolean readOnly,   // true = template padrão do sistema, não editável/apagável pela clínica
    boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
