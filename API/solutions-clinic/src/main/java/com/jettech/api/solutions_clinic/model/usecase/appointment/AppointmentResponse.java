package com.jettech.api.solutions_clinic.model.usecase.appointment;

import com.jettech.api.solutions_clinic.model.entity.AppointmentStatus;
import com.jettech.api.solutions_clinic.model.entity.PaymentMethod;
import com.jettech.api.solutions_clinic.model.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentResponse(
    UUID id,
    UUID tenantId,
    UUID patientId,
    UUID professionalId,
    UUID roomId,
    LocalDateTime scheduledAt,
    int durationMinutes,
    AppointmentStatus status,
    String observations,
    LocalDateTime cancelledAt,
    LocalDateTime startedAt,
    LocalDateTime finishedAt,
    Integer durationActualMinutes,
    BigDecimal totalValue,
    PaymentMethod paymentMethod,
    PaymentStatus paymentStatus,
    LocalDateTime paidAt,
    UUID createdBy,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
