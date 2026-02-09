package com.jettech.api.solutions_clinic.model.usecase.medicalrecordtemplate;

import com.jettech.api.solutions_clinic.model.entity.MedicalRecordTemplate;
import com.jettech.api.solutions_clinic.model.repository.MedicalRecordTemplateRepository;
import com.jettech.api.solutions_clinic.security.TenantContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.jettech.api.solutions_clinic.exception.AuthenticationFailedException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultGetMedicalRecordTemplatesByTenantUseCase implements GetMedicalRecordTemplatesByTenantUseCase {

    private final MedicalRecordTemplateRepository templateRepository;
    private final TenantContext tenantContext;

    @Override
    public List<MedicalRecordTemplateResponse> execute(GetMedicalRecordTemplatesByTenantRequest request) throws AuthenticationFailedException {
        tenantContext.requireSameTenant(request.tenantId());

        // Escopo de visibilidade: templates globais (tenant_id IS NULL) + templates da clínica
        List<MedicalRecordTemplate> templates;
        if (request.professionalType() != null && !request.professionalType().isBlank()) {
            templates = templateRepository.findAvailableForTenantAndProfessionalType(
                    request.tenantId(), request.professionalType());
        } else {
            templates = templateRepository.findAvailableForTenant(request.tenantId());
        }

        return templates.stream()
                .map(DefaultCreateMedicalRecordTemplateUseCase::toResponse)
                .toList();
    }
}
