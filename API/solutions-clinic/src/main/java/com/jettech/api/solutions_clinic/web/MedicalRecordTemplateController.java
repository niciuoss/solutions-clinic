package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.model.usecase.medicalrecordtemplate.CreateMedicalRecordTemplateRequest;
import com.jettech.api.solutions_clinic.model.usecase.medicalrecordtemplate.GetMedicalRecordTemplateByIdUseCase;
import com.jettech.api.solutions_clinic.model.usecase.medicalrecordtemplate.GetMedicalRecordTemplatesByTenantRequest;
import com.jettech.api.solutions_clinic.model.usecase.medicalrecordtemplate.GetMedicalRecordTemplatesByTenantUseCase;
import com.jettech.api.solutions_clinic.model.usecase.medicalrecordtemplate.MedicalRecordTemplateResponse;
import com.jettech.api.solutions_clinic.model.usecase.medicalrecordtemplate.CreateMedicalRecordTemplateUseCase;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jettech.api.solutions_clinic.exception.AuthenticationFailedException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class MedicalRecordTemplateController implements MedicalRecordTemplateAPI {

    private final CreateMedicalRecordTemplateUseCase createTemplateUseCase;
    private final GetMedicalRecordTemplateByIdUseCase getTemplateByIdUseCase;
    private final GetMedicalRecordTemplatesByTenantUseCase getTemplatesByTenantUseCase;

    @Override
    public MedicalRecordTemplateResponse createTemplate(@Valid @RequestBody CreateMedicalRecordTemplateRequest request) throws AuthenticationFailedException {
        return createTemplateUseCase.execute(request);
    }

    @Override
    public MedicalRecordTemplateResponse getTemplateById(@PathVariable UUID id) throws AuthenticationFailedException {
        return getTemplateByIdUseCase.execute(id);
    }

    @Override
    public List<MedicalRecordTemplateResponse> getTemplatesByTenant(
            @RequestParam UUID tenantId,
            @RequestParam(required = false, defaultValue = "true") boolean activeOnly,
            @RequestParam(required = false) String professionalType
    ) throws AuthenticationFailedException {
        return getTemplatesByTenantUseCase.execute(
                new GetMedicalRecordTemplatesByTenantRequest(tenantId, activeOnly, professionalType));
    }
}
