package com.jettech.api.solutions_clinic.model.usecase.tenant;

import com.jettech.api.solutions_clinic.model.entity.PlanType;
import com.jettech.api.solutions_clinic.model.entity.Tenant;
import com.jettech.api.solutions_clinic.model.entity.TenantStatus;
import com.jettech.api.solutions_clinic.model.repository.TenantRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultUpdateTenantPlanUseCase implements UpdateTenantPlanUseCase {

    private final TenantRepository tenantRepository;

    @Override
    @Transactional
    public TenantResponse execute(UpdateTenantPlanRequest request) throws AuthenticationException {
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new RuntimeException("Clínica não encontrada com ID: " + request.tenantId()));
        log.info("id clinica {}", request.tenantId());

        // Atualizar o plano
        tenant.setPlanType(request.planType());

        // Se estava em PENDING_SETUP e selecionou um plano, atualizar status para ACTIVE
        // Exceto se for CUSTOM (que requer aprovação)
        if (tenant.getStatus() == TenantStatus.PENDING_SETUP) {
            if (request.planType() != PlanType.CUSTOM) {
                tenant.setStatus(TenantStatus.ACTIVE);
            }
            // Se for CUSTOM, mantém PENDING_SETUP até aprovação manual
        }

        tenant = tenantRepository.save(tenant);

        return toResponse(tenant);
    }

    private TenantResponse toResponse(Tenant tenant) {
        return new TenantResponse(
                tenant.getId(),
                tenant.getName(),
                tenant.getCnpj(),
                tenant.getPlanType(),
                tenant.getAddress(),
                tenant.getPhone(),
                tenant.isActive(),
                tenant.getSubdomain(),
                tenant.getType(),
                tenant.getStatus(),
                tenant.getTrialEndsAt(),
                tenant.getCreatedAt(),
                tenant.getUpdatedAt()
        );
    }
}
