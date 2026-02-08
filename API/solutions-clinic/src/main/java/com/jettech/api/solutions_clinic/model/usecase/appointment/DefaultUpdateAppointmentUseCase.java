package com.jettech.api.solutions_clinic.model.usecase.appointment;

import com.jettech.api.solutions_clinic.model.entity.*;
import com.jettech.api.solutions_clinic.model.repository.*;
import com.jettech.api.solutions_clinic.model.service.FinancialSyncService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jettech.api.solutions_clinic.exception.ApiError;
import com.jettech.api.solutions_clinic.exception.AppointmentConflictException;
import com.jettech.api.solutions_clinic.exception.AuthenticationFailedException;
import com.jettech.api.solutions_clinic.exception.EntityNotFoundException;
import com.jettech.api.solutions_clinic.exception.InvalidStateException;
import com.jettech.api.solutions_clinic.exception.ScheduleValidationException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultUpdateAppointmentUseCase implements UpdateAppointmentUseCase {

    private static final String PROFESSIONAL_CONFLICT_MESSAGE =
            "Já existe um agendamento para este profissional neste horário. Deseja agendar mesmo assim?";
    private static final String ROOM_CONFLICT_MESSAGE =
            "Já existe um agendamento para esta sala neste horário. Deseja agendar mesmo assim?";

    private final AppointmentRepository appointmentRepository;
    private final AvailabilityConflictChecker availabilityConflictChecker;
    private final PatientRepository patientRepository;
    private final ProfessionalRepository professionalRepository;
    private final RoomRepository roomRepository;
    private final ProfessionalScheduleRepository professionalScheduleRepository;
    private final FinancialSyncService financialSyncService;

    @Override
    @Transactional
    public AppointmentResponse execute(UpdateAppointmentRequest request) throws AuthenticationFailedException {
        Appointment appointment = appointmentRepository.findById(request.id())
                .orElseThrow(() -> new EntityNotFoundException("Agendamento", request.id()));

        // Não permitir atualização de agendamentos cancelados ou finalizados
        if (appointment.getStatus() == AppointmentStatus.CANCELADO || 
            appointment.getStatus() == AppointmentStatus.FINALIZADO) {
            throw new InvalidStateException(ApiError.INVALID_STATE_APPOINTMENT_STATUS);
        }

        // Atualizar paciente se fornecido
        if (request.patientId() != null) {
            Patient patient = patientRepository.findById(request.patientId())
                    .orElseThrow(() -> new EntityNotFoundException("Paciente", request.patientId()));
            appointment.setPatient(patient);
        }

        // Atualizar profissional se fornecido
        if (request.professionalId() != null) {
            Professional professional = professionalRepository.findById(request.professionalId())
                    .orElseThrow(() -> new EntityNotFoundException("Profissional", request.professionalId()));
            appointment.setProfessional(professional);
        }

        // Atualizar sala se fornecido
        if (request.roomId() != null) {
            Room room = roomRepository.findById(request.roomId())
                    .orElseThrow(() -> new EntityNotFoundException("Sala", request.roomId()));
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
            
            // Verificar conflito de horário com outros agendamentos do profissional
            String professionalConflict = availabilityConflictChecker.findConflict(
                    scheduledAt,
                    durationMinutes,
                    appointment.getId(),
                    (start, end) -> appointmentRepository.findByProfessionalIdAndScheduledAtBetweenAndStatusNot(
                            professionalId, start, end, AppointmentStatus.CANCELADO),
                    PROFESSIONAL_CONFLICT_MESSAGE
            );
            if (professionalConflict != null && !request.forceSchedule()) {
                throw new AppointmentConflictException(professionalConflict);
            }

            // Verificar conflito de horário com outros agendamentos da sala (se fornecida)
            if (roomId != null) {
                String roomConflict = availabilityConflictChecker.findConflict(
                        scheduledAt,
                        durationMinutes,
                        appointment.getId(),
                        (start, end) -> appointmentRepository.findByRoomIdAndScheduledAtBetweenAndStatusNot(
                                roomId, start, end, AppointmentStatus.CANCELADO),
                        ROOM_CONFLICT_MESSAGE
                );
                if (roomConflict != null && !request.forceSchedule()) {
                    throw new AppointmentConflictException(roomConflict);
                }
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

        // Atualizar status de pagamento se fornecido
        PaymentStatus oldPaymentStatus = appointment.getPaymentStatus();
        if (request.paymentStatus() != null) {
            appointment.setPaymentStatus(request.paymentStatus());
            
            // Se foi marcado como PAGO, definir paidAt
            if (request.paymentStatus() == PaymentStatus.PAGO && appointment.getPaidAt() == null) {
                appointment.setPaidAt(LocalDateTime.now());
            }
            
            // Se foi cancelado, limpar paidAt
            if (request.paymentStatus() == PaymentStatus.CANCELADO) {
                appointment.setPaidAt(null);
            }
        }

        // Atualizar método de pagamento se fornecido
        if (request.paymentMethod() != null) {
            appointment.setPaymentMethod(request.paymentMethod());
        }

        appointment = appointmentRepository.save(appointment);

        // Sincronizar transação financeira se o status de pagamento mudou para PAGO
        if (request.paymentStatus() != null && 
            request.paymentStatus() == PaymentStatus.PAGO && 
            oldPaymentStatus != PaymentStatus.PAGO) {
            financialSyncService.syncAppointmentPayment(appointment);
        }

        return toResponse(appointment);
    }

    private void validateProfessionalSchedule(UUID professionalId, LocalDateTime scheduledAt, int durationMinutes) {
        DayOfWeek dayOfWeek = scheduledAt.getDayOfWeek();
        LocalTime scheduledTime = scheduledAt.toLocalTime();
        LocalDateTime endTime = scheduledAt.plusMinutes(durationMinutes);

        // Buscar agenda do profissional para o dia da semana
        ProfessionalSchedule schedule = professionalScheduleRepository
                .findByProfessionalIdAndDayOfWeek(professionalId, dayOfWeek)
                .orElseThrow(() -> new EntityNotFoundException(ApiError.ENTITY_NOT_FOUND_SCHEDULE));

        // Verificar se o horário está dentro do horário de trabalho
        if (scheduledTime.isBefore(schedule.getStartTime()) || 
            endTime.toLocalTime().isAfter(schedule.getEndTime())) {
            throw new ScheduleValidationException(ApiError.SCHEDULE_OUTSIDE_WORK_HOURS);
        }

        // Verificar se o horário não está no intervalo de almoço
        if ((scheduledTime.isAfter(schedule.getLunchBreakStart()) && scheduledTime.isBefore(schedule.getLunchBreakEnd())) ||
            (endTime.toLocalTime().isAfter(schedule.getLunchBreakStart()) && endTime.toLocalTime().isBefore(schedule.getLunchBreakEnd())) ||
            (scheduledTime.isBefore(schedule.getLunchBreakStart()) && endTime.toLocalTime().isAfter(schedule.getLunchBreakEnd()))) {
            throw new ScheduleValidationException(ApiError.SCHEDULE_IN_LUNCH_BREAK);
        }

        // Verificar se a duração é compatível com o slotDurationMinutes
        if (durationMinutes % schedule.getSlotDurationMinutes() != 0) {
            throw new ScheduleValidationException(ApiError.SCHEDULE_DURATION_MULTIPLE, schedule.getSlotDurationMinutes());
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
