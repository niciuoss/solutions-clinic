package com.jettech.api.solutions_clinic.model.usecase.appointment;

import com.jettech.api.solutions_clinic.model.entity.Appointment;
import com.jettech.api.solutions_clinic.model.repository.AppointmentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.jettech.api.solutions_clinic.exception.AuthenticationFailedException;
import com.jettech.api.solutions_clinic.exception.EntityNotFoundException;
import com.jettech.api.solutions_clinic.exception.ForbiddenException;
import com.jettech.api.solutions_clinic.security.TenantContext;
import java.util.UUID;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultGetAppointmentByIdUseCase implements GetAppointmentByIdUseCase {

    private final AppointmentRepository appointmentRepository;
    private final TenantContext tenantContext;

    @Override
    public AppointmentResponse execute(UUID id) throws AuthenticationFailedException {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agendamento", id));
        if (!appointment.getTenant().getId().equals(tenantContext.getRequiredClinicId())) {
            throw new ForbiddenException();
        }
        return toResponse(appointment);
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
