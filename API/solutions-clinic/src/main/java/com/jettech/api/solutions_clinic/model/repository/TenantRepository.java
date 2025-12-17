package com.jettech.api.solutions_clinic.model.repository;

import com.jettech.api.solutions_clinic.model.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    
    Optional<Tenant> findByCnpj(String cnpj);
    
    Optional<Tenant> findBySubdomain(String subdomain);
    
    List<Tenant> findByActive(boolean active);
    
    List<Tenant> findByType(com.jettech.api.solutions_clinic.model.entity.TypeTenant type);
}

