package com.jettech.api.solutions_clinic.model.usecase.professionalschedule;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.UUID;

public record UpdateProfessionalScheduleRequest(
    @NotNull(message = "O campo [id] é obrigatório")
    UUID id,
    
    @NotNull(message = "O campo [startTime] é obrigatório")
    LocalTime startTime,
    
    @NotNull(message = "O campo [endTime] é obrigatório")
    LocalTime endTime,
    
    @NotNull(message = "O campo [lunchBreakStart] é obrigatório")
    LocalTime lunchBreakStart,
    
    @NotNull(message = "O campo [lunchBreakEnd] é obrigatório")
    LocalTime lunchBreakEnd,
    
    @Min(value = 15, message = "O campo [slotDurationMinutes] deve ser no mínimo 15 minutos")
    @Max(value = 120, message = "O campo [slotDurationMinutes] deve ser no máximo 120 minutos")
    int slotDurationMinutes
) {
}
