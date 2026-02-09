package com.jettech.api.solutions_clinic.model.repository;

import com.jettech.api.solutions_clinic.model.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, UUID> {

    Optional<MedicalRecord> findByAppointmentId(UUID appointmentId);

    Optional<MedicalRecord> findByIdAndAppointment_TenantId(UUID id, UUID tenantId);
}
