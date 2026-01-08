package com.jettech.api.solutions_clinic.model.usecase.professionalschedule;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record ProfessionalScheduleResponse(
    UUID id,
    UUID professionalId,
    DayOfWeek dayOfWeek,
    LocalTime startTime,
    LocalTime endTime,
    LocalTime lunchBreakStart,
    LocalTime lunchBreakEnd,
    int slotDurationMinutes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
