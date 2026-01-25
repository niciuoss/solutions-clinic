package com.jettech.api.solutions_clinic.model.usecase.appointment;

import com.jettech.api.solutions_clinic.model.entity.Appointment;
import com.jettech.api.solutions_clinic.model.entity.AppointmentStatus;
import com.jettech.api.solutions_clinic.model.entity.ProfessionalSchedule;
import com.jettech.api.solutions_clinic.model.repository.AppointmentRepository;
import com.jettech.api.solutions_clinic.model.repository.ProfessionalRepository;
import com.jettech.api.solutions_clinic.model.repository.ProfessionalScheduleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultCheckAvailabilityUseCase implements CheckAvailabilityUseCase {

    private final AppointmentRepository appointmentRepository;
    private final ProfessionalRepository professionalRepository;
    private final ProfessionalScheduleRepository professionalScheduleRepository;

    @Override
    public Boolean execute(CheckAvailabilityRequest request) {
        // Validar se o profissional existe
        if (!professionalRepository.existsById(request.professionalId())) {
            throw new RuntimeException("Profissional não encontrado com ID: " + request.professionalId());
        }

        LocalDateTime scheduledAt = request.startTime();
        int durationMinutes = request.durationMinutes();
        LocalDateTime appointmentEnd = scheduledAt.plusMinutes(durationMinutes);
        DayOfWeek dayOfWeek = scheduledAt.getDayOfWeek();
        LocalTime scheduledTime = scheduledAt.toLocalTime();

        // Verificar se o profissional tem agenda para este dia da semana
        ProfessionalSchedule schedule = professionalScheduleRepository
                .findByProfessionalIdAndDayOfWeek(request.professionalId(), dayOfWeek)
                .orElse(null);

        if (schedule == null) {
            return false; // Profissional não trabalha neste dia
        }

        // Verificar se o horário está dentro do horário de trabalho
        if (scheduledTime.isBefore(schedule.getStartTime()) || 
            appointmentEnd.toLocalTime().isAfter(schedule.getEndTime())) {
            return false; // Fora do horário de trabalho
        }

        // Verificar se o horário não está no intervalo de almoço
        if ((scheduledTime.isAfter(schedule.getLunchBreakStart()) && scheduledTime.isBefore(schedule.getLunchBreakEnd())) ||
            (appointmentEnd.toLocalTime().isAfter(schedule.getLunchBreakStart()) && appointmentEnd.toLocalTime().isBefore(schedule.getLunchBreakEnd())) ||
            (scheduledTime.isBefore(schedule.getLunchBreakStart()) && appointmentEnd.toLocalTime().isAfter(schedule.getLunchBreakEnd()))) {
            return false; // No intervalo de almoço
        }

        // Verificar se a duração é compatível com o slotDurationMinutes
        if (durationMinutes % schedule.getSlotDurationMinutes() != 0) {
            return false; // Duração não é múltipla do slot
        }

        // Verificar conflitos com agendamentos existentes
        LocalDateTime searchStart = scheduledAt.minusHours(8);
        LocalDateTime searchEnd = appointmentEnd.plusHours(1);

        List<Appointment> existingAppointments = appointmentRepository
                .findByProfessionalIdAndScheduledAtBetweenAndStatusNot(
                        request.professionalId(),
                        searchStart,
                        searchEnd,
                        AppointmentStatus.CANCELADO
                );

        for (Appointment existing : existingAppointments) {
            if (request.excludeAppointmentId() != null && existing.getId().equals(request.excludeAppointmentId())) {
                continue; // Ignorar o próprio agendamento se estiver editando
            }

            LocalDateTime existingStart = existing.getScheduledAt();
            LocalDateTime existingEnd = existingStart.plusMinutes(existing.getDurationMinutes());

            // Verificar sobreposição: dois intervalos se sobrepõem se:
            // start1 < end2 && start2 < end1
            if (scheduledAt.isBefore(existingEnd) && existingStart.isBefore(appointmentEnd)) {
                return false; // Conflito de horário
            }
        }

        return true; // Horário disponível
    }
}
