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

import com.jettech.api.solutions_clinic.exception.EntityNotFoundException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultGetAvailableSlotsUseCase implements GetAvailableSlotsUseCase {

    private final AppointmentRepository appointmentRepository;
    private final ProfessionalRepository professionalRepository;
    private final ProfessionalScheduleRepository professionalScheduleRepository;

    @Override
    public List<String> execute(GetAvailableSlotsRequest request) {
        // Validar se o profissional existe
        if (!professionalRepository.existsById(request.professionalId())) {
            throw new EntityNotFoundException("Profissional", request.professionalId());
        }

        LocalDate date = request.date();
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        int durationMinutes = request.durationMinutes();

        // Buscar agenda do profissional para o dia da semana
        ProfessionalSchedule schedule = professionalScheduleRepository
                .findByProfessionalIdAndDayOfWeek(request.professionalId(), dayOfWeek)
                .orElse(null);

        if (schedule == null) {
            return new ArrayList<>(); // Profissional não trabalha neste dia
        }

        // Buscar agendamentos existentes do profissional neste dia
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.atTime(23, 59, 59);

        List<Appointment> existingAppointments = appointmentRepository
                .findByProfessionalIdAndScheduledAtBetweenAndStatusNot(
                        request.professionalId(),
                        dayStart,
                        dayEnd,
                        AppointmentStatus.CANCELADO
                );

        // Gerar todos os slots possíveis baseado no horário de trabalho e slotDurationMinutes
        List<String> availableSlots = new ArrayList<>();
        LocalTime currentTime = schedule.getStartTime();
        LocalTime endTime = schedule.getEndTime();
        int slotDuration = schedule.getSlotDurationMinutes();

        while (!currentTime.plusMinutes(durationMinutes).isAfter(endTime)) {
            
            // Verificar se o slot não está no intervalo de almoço
            LocalTime slotEnd = currentTime.plusMinutes(durationMinutes);
            if (!isInLunchBreak(currentTime, slotEnd, schedule.getLunchBreakStart(), schedule.getLunchBreakEnd())) {
                
                // Verificar se não há conflito com agendamentos existentes
                LocalDateTime slotStartDateTime = date.atTime(currentTime);
                LocalDateTime slotEndDateTime = date.atTime(slotEnd);
                
                boolean hasConflict = false;
                for (Appointment appointment : existingAppointments) {
                    LocalDateTime appointmentStart = appointment.getScheduledAt();
                    LocalDateTime appointmentEnd = appointmentStart.plusMinutes(appointment.getDurationMinutes());
                    
                    // Verificar sobreposição
                    if (slotStartDateTime.isBefore(appointmentEnd) && appointmentStart.isBefore(slotEndDateTime)) {
                        hasConflict = true;
                        break;
                    }
                }
                
                if (!hasConflict) {
                    // Adicionar slot no formato HH:mm
                    availableSlots.add(currentTime.format(DateTimeFormatter.ofPattern("HH:mm")));
                }
            }
            
            // Avançar para o próximo slot
            currentTime = currentTime.plusMinutes(slotDuration);
        }

        return availableSlots;
    }

    private boolean isInLunchBreak(LocalTime start, LocalTime end, LocalTime lunchStart, LocalTime lunchEnd) {
        // Verificar se o slot está no intervalo de almoço
        return (start.isAfter(lunchStart) && start.isBefore(lunchEnd)) ||
               (end.isAfter(lunchStart) && end.isBefore(lunchEnd)) ||
               (start.isBefore(lunchStart) && end.isAfter(lunchEnd));
    }
}
