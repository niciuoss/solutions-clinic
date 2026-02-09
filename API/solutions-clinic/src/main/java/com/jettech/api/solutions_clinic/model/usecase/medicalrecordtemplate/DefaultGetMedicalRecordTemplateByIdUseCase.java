package com.jettech.api.solutions_clinic.model.usecase.medicalrecordtemplate;

import com.jettech.api.solutions_clinic.model.repository.MedicalRecordTemplateRepository;
import com.jettech.api.solutions_clinic.security.TenantContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.jettech.api.solutions_clinic.exception.AuthenticationFailedException;
import com.jettech.api.solutions_clinic.exception.EntityNotFoundException;

import java.util.UUID;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultGetMedicalRecordTemplateByIdUseCase implements GetMedicalRecordTemplateByIdUseCase {

    private final MedicalRecordTemplateRepository templateRepository;
    private final TenantContext tenantContext;

    @Override
    public MedicalRecordTemplateResponse execute(UUID id) throws AuthenticationFailedException {
        UUID tenantId = tenantContext.getRequiredClinicId();
        // Visível se for template global (tenant_id IS NULL) ou da clínica
        return templateRepository.findByIdAvailableForTenant(id, tenantId)
                .map(DefaultCreateMedicalRecordTemplateUseCase::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Modelo de prontuário", id));
    }
}
