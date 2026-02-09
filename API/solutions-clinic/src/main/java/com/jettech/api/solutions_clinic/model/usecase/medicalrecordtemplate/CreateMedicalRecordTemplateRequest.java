package com.jettech.api.solutions_clinic.model.usecase.medicalrecordtemplate;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateMedicalRecordTemplateRequest(
    @NotNull(message = "O campo [tenantId] é obrigatório")
    UUID tenantId,

    @NotBlank(message = "O campo [name] é obrigatório")
    @Size(min = 1, max = 255)
    String name,

    @Size(max = 50)
    String professionalType,

    @NotNull(message = "O campo [schema] é obrigatório")
    JsonNode schema
) {}
