package com.jettech.api.solutions_clinic.model.usecase.medicalrecordtemplate;

import com.jettech.api.solutions_clinic.model.entity.MedicalRecordTemplate;
import com.jettech.api.solutions_clinic.model.entity.Tenant;
import com.jettech.api.solutions_clinic.model.repository.MedicalRecordTemplateRepository;
import com.jettech.api.solutions_clinic.model.repository.TenantRepository;
import com.jettech.api.solutions_clinic.security.TenantContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jettech.api.solutions_clinic.exception.AuthenticationFailedException;
import com.jettech.api.solutions_clinic.exception.EntityNotFoundException;

import java.util.UUID;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultCreateMedicalRecordTemplateUseCase implements CreateMedicalRecordTemplateUseCase {

    private final MedicalRecordTemplateRepository templateRepository;
    private final TenantRepository tenantRepository;
    private final TenantContext tenantContext;

    @Override
    @Transactional
    public MedicalRecordTemplateResponse execute(CreateMedicalRecordTemplateRequest request) throws AuthenticationFailedException {
        tenantContext.requireSameTenant(request.tenantId());

        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Clínica", request.tenantId()));

        MedicalRecordTemplate template = new MedicalRecordTemplate();
        template.setTenant(tenant);
        template.setName(request.name());
        template.setProfessionalType(request.professionalType());
        template.setSchema(request.schema());
        template.setActive(true);

        template = templateRepository.save(template);

        return toResponse(template);
    }

    static MedicalRecordTemplateResponse toResponse(MedicalRecordTemplate template) {
        return new MedicalRecordTemplateResponse(
                template.getId(),
                template.getTenant() != null ? template.getTenant().getId() : null,
                template.getName(),
                template.getProfessionalType(),
                template.getSchema(),
                template.isReadOnly(),
                template.isActive(),
                template.getCreatedAt(),
                template.getUpdatedAt()
        );
    }
}
