package com.jettech.api.solutions_clinic.model.usecase.medicalrecord;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.util.UUID;

public record MedicalRecordResponse(
    UUID id,
    UUID appointmentId,
    UUID templateId,
    JsonNode content,
    JsonNode vitalSigns,
    LocalDateTime signedAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
