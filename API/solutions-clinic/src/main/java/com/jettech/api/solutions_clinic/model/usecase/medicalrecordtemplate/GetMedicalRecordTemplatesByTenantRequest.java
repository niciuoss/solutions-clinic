package com.jettech.api.solutions_clinic.model.usecase.medicalrecordtemplate;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record GetMedicalRecordTemplatesByTenantRequest(
    @NotNull(message = "O campo [tenantId] é obrigatório")
    UUID tenantId,

    boolean activeOnly,

    String professionalType
) {}
