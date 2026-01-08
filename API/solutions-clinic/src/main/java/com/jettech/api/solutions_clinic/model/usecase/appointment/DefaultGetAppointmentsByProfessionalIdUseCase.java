package com.jettech.api.solutions_clinic.model.usecase.appointment;

import com.jettech.api.solutions_clinic.model.entity.Appointment;
import com.jettech.api.solutions_clinic.model.repository.AppointmentRepository;
import com.jettech.api.solutions_clinic.model.repository.ProfessionalRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultGetAppointmentsByProfessionalIdUseCase implements GetAppointmentsByProfessionalIdUseCase {

    private final AppointmentRepository appointmentRepository;
    private final ProfessionalRepository professionalRepository;

    @Override
    public List<AppointmentResponse> execute(UUID professionalId) throws AuthenticationException {
        // Validar se o profissional existe
        professionalRepository.findById(professionalId)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado com ID: " + professionalId));

        List<Appointment> appointments = appointmentRepository.findByProfessionalId(professionalId);

        return appointments.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
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
