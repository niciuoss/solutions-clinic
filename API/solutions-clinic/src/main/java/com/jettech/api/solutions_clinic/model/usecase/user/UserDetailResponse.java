package com.jettech.api.solutions_clinic.model.usecase.user;

import com.jettech.api.solutions_clinic.model.entity.Role;
import com.jettech.api.solutions_clinic.model.entity.TypeTenant;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserDetailResponse(
    UUID id,
    String firstName,
    String lastName,
    String email,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    List<TenantRoleInfo> tenantRoles
) {
    public record TenantRoleInfo(
        UUID tenantId,
        String tenantName,
        String subdomain,
        TypeTenant tenantType,
        boolean tenantActive,
        Role role
    ) {
    }
}

