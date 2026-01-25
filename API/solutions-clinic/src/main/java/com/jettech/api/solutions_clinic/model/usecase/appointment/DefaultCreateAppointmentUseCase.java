package com.jettech.api.solutions_clinic.model.usecase.appointment;

import com.jettech.api.solutions_clinic.model.entity.*;
import com.jettech.api.solutions_clinic.model.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultCreateAppointmentUseCase implements CreateAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final ProfessionalRepository professionalRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final ProfessionalScheduleRepository professionalScheduleRepository;
    private final ProcedureRepository procedureRepository;

    @Override
    @Transactional
    public AppointmentResponse execute(CreateAppointmentRequest request) throws AuthenticationException {
        // Validar se o tenant existe
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new RuntimeException("Clínica não encontrada com ID: " + request.tenantId()));

        // Validar se o paciente existe
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado com ID: " + request.patientId()));

        // Validar se o profissional existe
        Professional professional = professionalRepository.findById(request.professionalId())
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado com ID: " + request.professionalId()));

        // Validar se a sala existe (se fornecida)
        Room room = null;
        if (request.roomId() != null) {
            room = roomRepository.findById(request.roomId())
                    .orElseThrow(() -> new RuntimeException("Sala não encontrada com ID: " + request.roomId()));
        }

        // Validar se o usuário criador existe
        User createdBy = userRepository.findById(request.createdBy())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + request.createdBy()));

        // Validar e processar procedimentos se fornecidos
        List<Procedure> procedures = new ArrayList<>();
        int calculatedDurationMinutes = request.durationMinutes();
        BigDecimal calculatedTotalValue = request.totalValue();

        if (request.procedureIds() != null && !request.procedureIds().isEmpty()) {
            procedures = validateAndLoadProcedures(request.procedureIds(), request.tenantId());
            
            // Calcular duração total baseada nos procedimentos
            calculatedDurationMinutes = procedures.stream()
                    .mapToInt(Procedure::getEstimatedDurationMinutes)
                    .sum();
            
            // Calcular valor total baseado nos procedimentos (se totalValue não foi fornecido ou é zero)
            if (request.totalValue() == null || request.totalValue().compareTo(BigDecimal.ZERO) == 0) {
                calculatedTotalValue = procedures.stream()
                        .map(Procedure::getBasePrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            }
        }

        // Validar horário disponível do profissional
        validateProfessionalSchedule(professional.getId(), request.scheduledAt(), calculatedDurationMinutes);

        // Verificar conflito de horário com outros agendamentos do profissional
        String professionalConflict = checkProfessionalAvailabilityConflict(
                professional.getId(), 
                request.scheduledAt(), 
                calculatedDurationMinutes, 
                null
        );
        
        if (professionalConflict != null && !request.forceSchedule()) {
            throw new RuntimeException(professionalConflict);
        }

        // Verificar conflito de horário com outros agendamentos da sala (se fornecida)
        if (room != null) {
            String roomConflict = checkRoomAvailabilityConflict(
                    room.getId(), 
                    request.scheduledAt(), 
                    calculatedDurationMinutes, 
                    null
            );
            
            if (roomConflict != null && !request.forceSchedule()) {
                throw new RuntimeException(roomConflict);
            }
        }

        // Criar Appointment
        Appointment appointment = new Appointment();
        appointment.setTenant(tenant);
        appointment.setPatient(patient);
        appointment.setProfessional(professional);
        appointment.setRoom(room);
        appointment.setScheduledAt(request.scheduledAt());
        appointment.setDurationMinutes(calculatedDurationMinutes);
        appointment.setStatus(AppointmentStatus.AGENDADO);
        appointment.setObservations(request.observations());
        appointment.setTotalValue(calculatedTotalValue);
        appointment.setPaymentStatus(PaymentStatus.PENDENTE);
        appointment.setCreatedBy(createdBy);

        appointment = appointmentRepository.save(appointment);

        // Criar AppointmentProcedure para cada procedimento
        if (!procedures.isEmpty()) {
            for (Procedure procedure : procedures) {
                AppointmentProcedure appointmentProcedure = new AppointmentProcedure();
                appointmentProcedure.setAppointment(appointment);
                appointmentProcedure.setProcedure(procedure);
                appointmentProcedure.setFinalPrice(procedure.getBasePrice()); // Inicialmente usa o preço base
                appointment.getProcedures().add(appointmentProcedure);
            }
            appointment = appointmentRepository.save(appointment);
        }

        // Converter para Response
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

    private String checkProfessionalAvailabilityConflict(UUID professionalId, LocalDateTime scheduledAt, int durationMinutes, UUID excludeAppointmentId) {
        LocalDateTime appointmentEnd = scheduledAt.plusMinutes(durationMinutes);
        LocalDateTime searchStart = scheduledAt.minusHours(8); // Buscar até 8 horas antes
        LocalDateTime searchEnd = appointmentEnd.plusHours(1); // Buscar até 1 hora depois

        List<Appointment> existingAppointments = appointmentRepository
                .findByProfessionalIdAndScheduledAtBetweenAndStatusNot(
                        professionalId,
                        searchStart,
                        searchEnd,
                        AppointmentStatus.CANCELADO
                );

        // Verificar conflitos com agendamentos existentes
        for (Appointment existing : existingAppointments) {
            if (excludeAppointmentId != null && existing.getId().equals(excludeAppointmentId)) {
                continue;
            }

            LocalDateTime existingStart = existing.getScheduledAt();
            LocalDateTime existingEnd = existingStart.plusMinutes(existing.getDurationMinutes());

            // Verificar sobreposição: dois intervalos se sobrepõem se:
            // start1 < end2 && start2 < end1
            if (scheduledAt.isBefore(existingEnd) && existingStart.isBefore(appointmentEnd)) {
                return "Já existe um agendamento para este profissional neste horário. Deseja agendar mesmo assim?";
            }
        }
        
        return null; // Sem conflito
    }

    private void validateProfessionalAvailability(UUID professionalId, LocalDateTime scheduledAt, int durationMinutes, UUID excludeAppointmentId) {
        String conflict = checkProfessionalAvailabilityConflict(professionalId, scheduledAt, durationMinutes, excludeAppointmentId);
        if (conflict != null) {
            throw new RuntimeException(conflict);
        }
    }

    private List<Procedure> validateAndLoadProcedures(List<UUID> procedureIds, UUID tenantId) {
        List<Procedure> procedures = new ArrayList<>();
        
        for (UUID procedureId : procedureIds) {
            Procedure procedure = procedureRepository.findByIdAndTenantId(procedureId, tenantId)
                    .orElseThrow(() -> new RuntimeException("Procedimento não encontrado com ID: " + procedureId + " para o tenant: " + tenantId));
            
            if (!procedure.isActive()) {
                throw new RuntimeException("O procedimento " + procedure.getName() + " está inativo");
            }
            
            procedures.add(procedure);
        }
        
        return procedures;
    }

    private String checkRoomAvailabilityConflict(UUID roomId, LocalDateTime scheduledAt, int durationMinutes, UUID excludeAppointmentId) {
        LocalDateTime appointmentEnd = scheduledAt.plusMinutes(durationMinutes);
        LocalDateTime searchStart = scheduledAt.minusHours(8); // Buscar até 8 horas antes
        LocalDateTime searchEnd = appointmentEnd.plusHours(1); // Buscar até 1 hora depois

        List<Appointment> existingAppointments = appointmentRepository
                .findByRoomIdAndScheduledAtBetweenAndStatusNot(
                        roomId,
                        searchStart,
                        searchEnd,
                        AppointmentStatus.CANCELADO
                );

        // Verificar conflitos com agendamentos existentes
        for (Appointment existing : existingAppointments) {
            if (excludeAppointmentId != null && existing.getId().equals(excludeAppointmentId)) {
                continue;
            }

            LocalDateTime existingStart = existing.getScheduledAt();
            LocalDateTime existingEnd = existingStart.plusMinutes(existing.getDurationMinutes());

            // Verificar sobreposição: dois intervalos se sobrepõem se:
            // start1 < end2 && start2 < end1
            if (scheduledAt.isBefore(existingEnd) && existingStart.isBefore(appointmentEnd)) {
                return "Já existe um agendamento para esta sala neste horário. Deseja agendar mesmo assim?";
            }
        }
        
        return null; // Sem conflito
    }

    private void validateRoomAvailability(UUID roomId, LocalDateTime scheduledAt, int durationMinutes, UUID excludeAppointmentId) {
        String conflict = checkRoomAvailabilityConflict(roomId, scheduledAt, durationMinutes, excludeAppointmentId);
        if (conflict != null) {
            throw new RuntimeException(conflict);
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
