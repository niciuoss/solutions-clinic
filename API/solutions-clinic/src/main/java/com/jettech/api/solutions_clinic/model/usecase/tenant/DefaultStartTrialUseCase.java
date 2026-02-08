package com.jettech.api.solutions_clinic.model.usecase.tenant;

import com.jettech.api.solutions_clinic.model.entity.Tenant;
import com.jettech.api.solutions_clinic.model.entity.TenantStatus;
import com.jettech.api.solutions_clinic.model.repository.TenantRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jettech.api.solutions_clinic.exception.AuthenticationFailedException;
import com.jettech.api.solutions_clinic.exception.EntityNotFoundException;
import com.jettech.api.solutions_clinic.exception.InvalidStateException;
import java.time.LocalDate;

/**
 * UseCase para iniciar um período de teste (trial) para um tenant.
 * Define uma duração padrão de trial (configurável) e ativa o tenant com status TRIAL.
 */
@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultStartTrialUseCase implements StartTrialUseCase {

    private final TenantRepository tenantRepository;

    @Value("${app.trial.duration.days:14}")
    private int trialDurationDays;

    @Override
    @Transactional
    public TenantResponse execute(StartTrialRequest request) throws AuthenticationFailedException {
        log.info("Iniciando trial - tenantId: {}, duração: {} dias", request.tenantId(), trialDurationDays);

        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Clínica", request.tenantId()));

        // Verificar se já tem um plano ativo ou está em trial
        if (tenant.getStatus() == TenantStatus.ACTIVE || tenant.getStatus() == TenantStatus.TRIAL) {
            throw new InvalidStateException("A clínica já possui um plano ativo ou está em período de teste");
        }

        // Verificar se já teve trial anteriormente (opcional - pode remover se permitir múltiplos trials)
        if (tenant.getTrialEndsAt() != null && tenant.getTrialEndsAt().isAfter(LocalDate.now())) {
            throw new InvalidStateException("A clínica já está em período de teste");
        }

        // Definir data de término do trial
        LocalDate trialEndsAt = LocalDate.now().plusDays(trialDurationDays);

        // Atualizar tenant para status TRIAL
        tenant.setStatus(TenantStatus.TRIAL);
        tenant.setTrialEndsAt(trialEndsAt);
        tenant.setActive(true);
        // Não definimos planType para trial, pois é um status temporário

        tenant = tenantRepository.save(tenant);

        log.info("Trial iniciado com sucesso - tenantId: {}, trialEndsAt: {}, status: {}",
                tenant.getId(), tenant.getTrialEndsAt(), tenant.getStatus());

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
