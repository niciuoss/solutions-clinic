package com.jettech.api.solutions_clinic.model.usecase.appointment;

import com.jettech.api.solutions_clinic.model.entity.*;
import com.jettech.api.solutions_clinic.model.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultUpdateAppointmentUseCase implements UpdateAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final ProfessionalRepository professionalRepository;
    private final RoomRepository roomRepository;
    private final ProfessionalScheduleRepository professionalScheduleRepository;

    @Override
    @Transactional
    public AppointmentResponse execute(UpdateAppointmentRequest request) throws AuthenticationException {
        Appointment appointment = appointmentRepository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado com ID: " + request.id()));

        // Não permitir atualização de agendamentos cancelados ou finalizados
        if (appointment.getStatus() == AppointmentStatus.CANCELADO || 
            appointment.getStatus() == AppointmentStatus.FINALIZADO) {
            throw new RuntimeException("Não é possível atualizar um agendamento " + appointment.getStatus());
        }

        // Atualizar paciente se fornecido
        if (request.patientId() != null) {
            Patient patient = patientRepository.findById(request.patientId())
                    .orElseThrow(() -> new RuntimeException("Paciente não encontrado com ID: " + request.patientId()));
            appointment.setPatient(patient);
        }

        // Atualizar profissional se fornecido
        if (request.professionalId() != null) {
            Professional professional = professionalRepository.findById(request.professionalId())
                    .orElseThrow(() -> new RuntimeException("Profissional não encontrado com ID: " + request.professionalId()));
            appointment.setProfessional(professional);
        }

        // Atualizar sala se fornecido
        if (request.roomId() != null) {
            Room room = roomRepository.findById(request.roomId())
                    .orElseThrow(() -> new RuntimeException("Sala não encontrada com ID: " + request.roomId()));
            appointment.setRoom(room);
        }

        // Atualizar horário se fornecido
        LocalDateTime scheduledAt = request.scheduledAt() != null ? request.scheduledAt() : appointment.getScheduledAt();
        int durationMinutes = request.durationMinutes() != null ? request.durationMinutes() : appointment.getDurationMinutes();
        UUID professionalId = request.professionalId() != null ? request.professionalId() : appointment.getProfessional().getId();
        UUID roomId = request.roomId() != null ? request.roomId() : (appointment.getRoom() != null ? appointment.getRoom().getId() : null);

        // Validar horário disponível se horário ou profissional mudou
        if (request.scheduledAt() != null || request.professionalId() != null || request.durationMinutes() != null) {
            validateProfessionalSchedule(professionalId, scheduledAt, durationMinutes);
            validateProfessionalAvailability(professionalId, scheduledAt, durationMinutes, appointment.getId());
            
            if (roomId != null) {
                validateRoomAvailability(roomId, scheduledAt, durationMinutes, appointment.getId());
            }
        }

        appointment.setScheduledAt(scheduledAt);
        appointment.setDurationMinutes(durationMinutes);

        // Atualizar observações se fornecido
        if (request.observations() != null) {
            appointment.setObservations(request.observations());
        }

        // Atualizar valor total se fornecido
        if (request.totalValue() != null) {
            appointment.setTotalValue(request.totalValue());
        }

        appointment = appointmentRepository.save(appointment);

        return toResponse(appointment);
    }

    private void validateProfessionalSchedule(UUID professionalId, LocalDateTime scheduledAt, int durationMinutes) {
        DayOfWeek dayOfWeek = scheduledAt.getDayOfWeek();
        LocalTime scheduledTime = scheduledAt.toLocalTime();
        LocalDateTime endTime = scheduledAt.plusMinutes(durationMinutes);

        // Buscar agenda do profissional para o dia da semana
        ProfessionalSchedule schedule = professionalScheduleRepository
                .findByProfessionalIdAndDayOfWeek(professionalId, dayOfWeek)
                .orElseThrow(() -> new RuntimeException("O profissional não possui agenda cadastrada para " + dayOfWeek));

        // Verificar se o horário está dentro do horário de trabalho
        if (scheduledTime.isBefore(schedule.getStartTime()) || 
            endTime.toLocalTime().isAfter(schedule.getEndTime())) {
            throw new RuntimeException("O horário agendado está fora do horário de trabalho do profissional");
        }

        // Verificar se o horário não está no intervalo de almoço
        if ((scheduledTime.isAfter(schedule.getLunchBreakStart()) && scheduledTime.isBefore(schedule.getLunchBreakEnd())) ||
            (endTime.toLocalTime().isAfter(schedule.getLunchBreakStart()) && endTime.toLocalTime().isBefore(schedule.getLunchBreakEnd())) ||
            (scheduledTime.isBefore(schedule.getLunchBreakStart()) && endTime.toLocalTime().isAfter(schedule.getLunchBreakEnd()))) {
            throw new RuntimeException("O horário agendado está no intervalo de almoço do profissional");
        }

        // Verificar se a duração é compatível com o slotDurationMinutes
        if (durationMinutes % schedule.getSlotDurationMinutes() != 0) {
            throw new RuntimeException("A duração do agendamento deve ser múltipla de " + schedule.getSlotDurationMinutes() + " minutos");
        }
    }

    private void validateProfessionalAvailability(UUID professionalId, LocalDateTime scheduledAt, int durationMinutes, UUID excludeAppointmentId) {
        LocalDateTime appointmentEnd = scheduledAt.plusMinutes(durationMinutes);
        LocalDateTime searchStart = scheduledAt.minusHours(8);
        LocalDateTime searchEnd = appointmentEnd.plusHours(1);

        List<Appointment> existingAppointments = appointmentRepository
                .findByProfessionalIdAndScheduledAtBetweenAndStatusNot(
                        professionalId,
                        searchStart,
                        searchEnd,
                        AppointmentStatus.CANCELADO
                );

        for (Appointment existing : existingAppointments) {
            if (excludeAppointmentId != null && existing.getId().equals(excludeAppointmentId)) {
                continue;
            }

            LocalDateTime existingStart = existing.getScheduledAt();
            LocalDateTime existingEnd = existingStart.plusMinutes(existing.getDurationMinutes());

            if (scheduledAt.isBefore(existingEnd) && existingStart.isBefore(appointmentEnd)) {
                throw new RuntimeException("Já existe um agendamento para este profissional neste horário");
            }
        }
    }

    private void validateRoomAvailability(UUID roomId, LocalDateTime scheduledAt, int durationMinutes, UUID excludeAppointmentId) {
        LocalDateTime appointmentEnd = scheduledAt.plusMinutes(durationMinutes);
        LocalDateTime searchStart = scheduledAt.minusHours(8);
        LocalDateTime searchEnd = appointmentEnd.plusHours(1);

        List<Appointment> existingAppointments = appointmentRepository
                .findByRoomIdAndScheduledAtBetweenAndStatusNot(
                        roomId,
                        searchStart,
                        searchEnd,
                        AppointmentStatus.CANCELADO
                );

        for (Appointment existing : existingAppointments) {
            if (excludeAppointmentId != null && existing.getId().equals(excludeAppointmentId)) {
                continue;
            }

            LocalDateTime existingStart = existing.getScheduledAt();
            LocalDateTime existingEnd = existingStart.plusMinutes(existing.getDurationMinutes());

            if (scheduledAt.isBefore(existingEnd) && existingStart.isBefore(appointmentEnd)) {
                throw new RuntimeException("Já existe um agendamento para esta sala neste horário");
            }
        }
    }

    private AppointmentResponse toResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getTenant().getId(),
                appointment.getPatient().getId(),
                appointment.getProfessional().getId(),
                appointment.getRoom() != null ? appointment.getRoom().getId() : null,
                appointment.getScheduledAt(),
                appointment.getDurationMinutes(),
                appointment.getStatus(),
                appointment.getObservations(),
                appointment.getCancelledAt(),
                appointment.getStartedAt(),
                appointment.getFinishedAt(),
                appointment.getDurationActualMinutes(),
                appointment.getTotalValue(),
                appointment.getPaymentMethod(),
                appointment.getPaymentStatus(),
                appointment.getPaidAt(),
                appointment.getCreatedBy().getId(),
                appointment.getCreatedAt(),
                appointment.getUpdatedAt()
        );
    }
}
