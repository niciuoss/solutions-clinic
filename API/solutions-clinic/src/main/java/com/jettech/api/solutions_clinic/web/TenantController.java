package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.model.entity.Role;
import com.jettech.api.solutions_clinic.model.usecase.subscription.CreateCheckoutSessionBody;
import com.jettech.api.solutions_clinic.model.usecase.subscription.CreateCheckoutSessionRequest;
import com.jettech.api.solutions_clinic.model.usecase.subscription.CreateCheckoutSessionResponse;
import com.jettech.api.solutions_clinic.model.usecase.subscription.DefaultCreateCheckoutSessionUseCase;
import com.jettech.api.solutions_clinic.model.usecase.tenant.ActivatePlanBody;
import com.jettech.api.solutions_clinic.model.usecase.tenant.ActivatePlanRequest;
import com.jettech.api.solutions_clinic.model.usecase.tenant.DefaultActivatePlanUseCase;
import com.jettech.api.solutions_clinic.model.usecase.tenant.DefaultStartTrialUseCase;
import com.jettech.api.solutions_clinic.model.usecase.tenant.DefaultUpdateTenantPlanUseCase;
import com.jettech.api.solutions_clinic.model.usecase.tenant.StartTrialRequest;
import com.jettech.api.solutions_clinic.model.usecase.tenant.TenantResponse;
import com.jettech.api.solutions_clinic.model.usecase.tenant.UpdateTenantPlanBody;
import com.jettech.api.solutions_clinic.model.usecase.tenant.UpdateTenantPlanRequest;
import com.jettech.api.solutions_clinic.model.usecase.user.AssociateUserToTenantRequest;
import com.jettech.api.solutions_clinic.model.usecase.user.DefaultAssociateUserToTenantUseCase;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jettech.api.solutions_clinic.exception.AuthenticationFailedException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class TenantController implements TenantAPI {

    private final DefaultUpdateTenantPlanUseCase updateTenantPlanUseCase;
    private final DefaultCreateCheckoutSessionUseCase createCheckoutSessionUseCase;
    private final DefaultAssociateUserToTenantUseCase associateUserToTenantUseCase;
    private final DefaultActivatePlanUseCase activatePlanUseCase;
    private final DefaultStartTrialUseCase startTrialUseCase;

    @Override
    public TenantResponse updateTenantPlan(
            @PathVariable UUID tenantId,
            @Valid @RequestBody UpdateTenantPlanBody body
    ) throws AuthenticationFailedException {
        log.info("Recebendo atualização de plano - tenantId: {}, planType: {}", tenantId, body.planType());
        // Criar request com tenantId do path e planType do body
        UpdateTenantPlanRequest request = new UpdateTenantPlanRequest(tenantId, body.planType());
        return updateTenantPlanUseCase.execute(request);
    }

    @Override
    public CreateCheckoutSessionResponse createCheckoutSession(
            @PathVariable UUID tenantId,
            @Valid @RequestBody CreateCheckoutSessionBody body
    ) throws AuthenticationFailedException {
        log.info("Criando sessão de checkout - tenantId: {}, planType: {}", tenantId, body.planType());
        CreateCheckoutSessionRequest request = new CreateCheckoutSessionRequest(tenantId, body.planType());
        return createCheckoutSessionUseCase.execute(request);
    }

    @Override
    public void associateUserToTenant(
            @PathVariable UUID tenantId,
            @PathVariable UUID userId,
            @PathVariable String role
    ) throws AuthenticationFailedException {
        log.info("Associando usuário à clínica - tenantId: {}, userId: {}, role: {}", tenantId, userId, role);
        
        // Converter String para Role enum
        Role roleEnum;
        try {
            roleEnum = Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Papel inválido: " + role + ". Papéis válidos: OWNER, ADMIN, RECEPTION, SPECIALIST, FINANCE, READONLY");
        }
        
        AssociateUserToTenantRequest request = new AssociateUserToTenantRequest(userId, tenantId, roleEnum);
        associateUserToTenantUseCase.execute(request);
    }

    @Override
    public TenantResponse activatePlan(
            @PathVariable UUID tenantId,
            @Valid @RequestBody ActivatePlanBody body
    ) throws AuthenticationFailedException {
        log.warn("ATIVACAO MANUAL - tenantId: {}, planType: {} - USE APENAS PARA TESTES!", tenantId, body.planType());
        ActivatePlanRequest request = new ActivatePlanRequest(tenantId, body.planType());
        return activatePlanUseCase.execute(request);
    }

    @Override
    public TenantResponse startTrial(
            @PathVariable UUID tenantId
    ) throws AuthenticationFailedException {
        log.info("Iniciando trial - tenantId: {}", tenantId);
        StartTrialRequest request = new StartTrialRequest(tenantId);
        return startTrialUseCase.execute(request);
    }
}
