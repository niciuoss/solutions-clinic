package com.jettech.api.solutions_clinic.model.usecase.professional;

import com.jettech.api.solutions_clinic.model.entity.TypeTenant;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ProfessionalTenantResponse(
    List<TenantInfo> tenants
) {
    public record TenantInfo(
        UUID tenantId,
        String tenantName,
        String cnpj,
        String subdomain,
        TypeTenant type,
        boolean active,
        UUID professionalId,
        String specialty,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
    }
}

