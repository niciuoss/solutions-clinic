package com.jettech.api.solutions_clinic.model.usecase.appointment;

import com.jettech.api.solutions_clinic.exception.ApiError;
import com.jettech.api.solutions_clinic.exception.EntityNotFoundException;
import com.jettech.api.solutions_clinic.exception.ScheduleValidationException;
import com.jettech.api.solutions_clinic.model.entity.ProfessionalSchedule;
import com.jettech.api.solutions_clinic.model.repository.ProfessionalScheduleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ProfessionalScheduleValidator {

    private final ProfessionalScheduleRepository professionalScheduleRepository;

    public void validate(UUID professionalId, LocalDateTime scheduledAt, int durationMinutes) {
        DayOfWeek dayOfWeek = scheduledAt.getDayOfWeek();
        LocalTime scheduledTime = scheduledAt.toLocalTime();
        LocalDateTime endTime = scheduledAt.plusMinutes(durationMinutes);

        ProfessionalSchedule professionalSchedule = professionalScheduleRepository
                .findByProfessionalIdAndDayOfWeek(professionalId, dayOfWeek)
                .orElseThrow(() -> new EntityNotFoundException(ApiError.ENTITY_NOT_FOUND_SCHEDULE));

        if (scheduledTime.isBefore(professionalSchedule.getStartTime()) ||
                endTime.toLocalTime().isAfter(professionalSchedule.getEndTime())) {
            throw new ScheduleValidationException(ApiError.SCHEDULE_OUTSIDE_WORK_HOURS);
        }

        if ((scheduledTime.isAfter(professionalSchedule.getLunchBreakStart()) && scheduledTime.isBefore(professionalSchedule.getLunchBreakEnd())) ||
                (endTime.toLocalTime().isAfter(professionalSchedule.getLunchBreakStart()) && endTime.toLocalTime().isBefore(professionalSchedule.getLunchBreakEnd())) ||
                (scheduledTime.isBefore(professionalSchedule.getLunchBreakStart()) && endTime.toLocalTime().isAfter(professionalSchedule.getLunchBreakEnd()))) {
            throw new ScheduleValidationException(ApiError.SCHEDULE_IN_LUNCH_BREAK);
        }

        if (durationMinutes % professionalSchedule.getSlotDurationMinutes() != 0) {
            throw new ScheduleValidationException(ApiError.SCHEDULE_DURATION_MULTIPLE, professionalSchedule.getSlotDurationMinutes());
        }
    }
}
