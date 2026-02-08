package com.jettech.api.solutions_clinic.model.usecase.appointment;

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
                .orElseThrow(() -> new EntityNotFoundException("O profissional não possui agenda cadastrada para " + dayOfWeek));

        if (scheduledTime.isBefore(professionalSchedule.getStartTime()) ||
                endTime.toLocalTime().isAfter(professionalSchedule.getEndTime())) {
            throw new ScheduleValidationException("O horário agendado está fora do horário de trabalho do profissional");
        }

        if ((scheduledTime.isAfter(professionalSchedule.getLunchBreakStart()) && scheduledTime.isBefore(professionalSchedule.getLunchBreakEnd())) ||
                (endTime.toLocalTime().isAfter(professionalSchedule.getLunchBreakStart()) && endTime.toLocalTime().isBefore(professionalSchedule.getLunchBreakEnd())) ||
                (scheduledTime.isBefore(professionalSchedule.getLunchBreakStart()) && endTime.toLocalTime().isAfter(professionalSchedule.getLunchBreakEnd()))) {
            throw new ScheduleValidationException("O horário agendado está no intervalo de almoço do profissional");
        }

        if (durationMinutes % professionalSchedule.getSlotDurationMinutes() != 0) {
            throw new ScheduleValidationException("A duração do agendamento deve ser múltipla de " + professionalSchedule.getSlotDurationMinutes() + " minutos");
        }
    }
}
