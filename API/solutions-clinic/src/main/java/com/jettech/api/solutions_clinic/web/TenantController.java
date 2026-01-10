package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.model.usecase.tenant.DefaultUpdateTenantPlanUseCase;
import com.jettech.api.solutions_clinic.model.usecase.tenant.TenantResponse;
import com.jettech.api.solutions_clinic.model.usecase.tenant.UpdateTenantPlanBody;
import com.jettech.api.solutions_clinic.model.usecase.tenant.UpdateTenantPlanRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class TenantController implements TenantAPI {

    private final DefaultUpdateTenantPlanUseCase updateTenantPlanUseCase;

    @Override
    public TenantResponse updateTenantPlan(
            @PathVariable UUID tenantId,
            @Valid @RequestBody UpdateTenantPlanBody body
    ) throws AuthenticationException {
        log.info("Recebendo atualização de plano - tenantId: {}, planType: {}", tenantId, body.planType());
        // Criar request com tenantId do path e planType do body
        UpdateTenantPlanRequest request = new UpdateTenantPlanRequest(tenantId, body.planType());
        return updateTenantPlanUseCase.execute(request);
    }
}
