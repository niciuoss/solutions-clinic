package com.jettech.api.solutions_clinic.model.usecase.appointment;

import com.jettech.api.solutions_clinic.model.entity.Appointment;
import com.jettech.api.solutions_clinic.model.entity.AppointmentStatus;
import com.jettech.api.solutions_clinic.model.repository.AppointmentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultDeleteAppointmentUseCase implements DeleteAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;

    @Override
    @Transactional
    public void execute(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado com ID: " + id));

        // Ao invés de deletar, marca como cancelado
        appointment.setStatus(AppointmentStatus.CANCELADO);
        appointment.setCancelledAt(LocalDateTime.now());

        appointmentRepository.save(appointment);
    }
}
