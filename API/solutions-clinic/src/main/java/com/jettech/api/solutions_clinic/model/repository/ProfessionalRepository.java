package com.jettech.api.solutions_clinic.model.repository;

import com.jettech.api.solutions_clinic.model.entity.Professional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfessionalRepository extends JpaRepository<Professional, UUID> {
    
    Optional<Professional> findByUserIdAndTenantId(UUID userId, UUID tenantId);
    
    List<Professional> findByTenantId(UUID tenantId);
    
    List<Professional> findByUserId(UUID userId);
    
    List<Professional> findByActive(boolean active);
}

