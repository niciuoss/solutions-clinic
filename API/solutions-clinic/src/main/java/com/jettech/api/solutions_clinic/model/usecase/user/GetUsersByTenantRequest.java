package com.jettech.api.solutions_clinic.model.usecase.user;

import java.util.UUID;

public record GetUsersByTenantRequest(
    UUID tenantId,
    int page,
    int size,
    String sort,
    String search,
    Boolean blocked
) {
}
