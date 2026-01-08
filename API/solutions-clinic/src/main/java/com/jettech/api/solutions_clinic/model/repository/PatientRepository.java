package com.jettech.api.solutions_clinic.model.repository;

import com.jettech.api.solutions_clinic.model.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    
    Optional<Patient> findByCpfAndTenantId(String cpf, UUID tenantId);
    
    List<Patient> findByTenantId(UUID tenantId);
    
    List<Patient> findByTenantIdAndIsActive(UUID tenantId, boolean isActive);
    
    boolean existsByCpfAndTenantId(String cpf, UUID tenantId);
}

