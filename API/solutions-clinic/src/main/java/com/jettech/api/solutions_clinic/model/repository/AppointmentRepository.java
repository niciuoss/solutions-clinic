package com.jettech.api.solutions_clinic.model.repository;

import com.jettech.api.solutions_clinic.model.entity.Appointment;
import com.jettech.api.solutions_clinic.model.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    
    List<Appointment> findByProfessionalId(UUID professionalId);
    
    List<Appointment> findByPatientId(UUID patientId);
    
    List<Appointment> findByRoomId(UUID roomId);
    
    List<Appointment> findByProfessionalIdAndScheduledAtBetween(
            UUID professionalId, 
            LocalDateTime start, 
            LocalDateTime end
    );
    
    List<Appointment> findByProfessionalIdAndScheduledAtBetweenAndStatusNot(
            UUID professionalId,
            LocalDateTime start,
            LocalDateTime end,
            AppointmentStatus status
    );
    
    List<Appointment> findByRoomIdAndScheduledAtBetweenAndStatusNot(
            UUID roomId,
            LocalDateTime start,
            LocalDateTime end,
            AppointmentStatus status
    );
    
    Optional<Appointment> findByIdAndStatus(UUID id, AppointmentStatus status);
    
    // Métodos para buscar por tenant com filtros
    List<Appointment> findByTenantId(UUID tenantId);
    
    List<Appointment> findByTenantIdAndScheduledAtBetween(
            UUID tenantId,
            LocalDateTime start,
            LocalDateTime end
    );
    
    List<Appointment> findByTenantIdAndStatus(
            UUID tenantId,
            AppointmentStatus status
    );
    
    List<Appointment> findByTenantIdAndScheduledAtBetweenAndStatus(
            UUID tenantId,
            LocalDateTime start,
            LocalDateTime end,
            AppointmentStatus status
    );
}
