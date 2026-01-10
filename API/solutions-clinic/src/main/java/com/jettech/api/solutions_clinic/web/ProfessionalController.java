package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.model.usecase.professional.AddProfessionalToClinicBodyRequest;
import com.jettech.api.solutions_clinic.model.usecase.professional.AddProfessionalToClinicRequest;
import com.jettech.api.solutions_clinic.model.usecase.professional.CreateProfessionalRequest;
import com.jettech.api.solutions_clinic.model.usecase.professional.DefaultAddProfessionalToClinicUseCase;
import com.jettech.api.solutions_clinic.model.usecase.professional.DefaultCreateProfessionalUseCase;
import com.jettech.api.solutions_clinic.model.usecase.professional.DefaultGetProfessionalTenantsUseCase;
import com.jettech.api.solutions_clinic.model.usecase.professional.ProfessionalResponse;
import com.jettech.api.solutions_clinic.model.usecase.professional.ProfessionalTenantResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ProfessionalController implements ProfessionalAPI {

    private final DefaultCreateProfessionalUseCase createProfessionalUseCase;
    private final DefaultAddProfessionalToClinicUseCase addProfessionalToClinicUseCase;
    private final DefaultGetProfessionalTenantsUseCase getProfessionalTenantsUseCase;

    @Override
    public ProfessionalResponse createProfessional(@Valid @RequestBody CreateProfessionalRequest request) throws AuthenticationException {
        return createProfessionalUseCase.execute(request);
    }

    @Override
    public ProfessionalResponse addProfessionalToClinic(
            @PathVariable UUID clinicId,
            @Valid @RequestBody AddProfessionalToClinicBodyRequest request) throws AuthenticationException {
        return addProfessionalToClinicUseCase.execute(new AddProfessionalToClinicRequest(
                request.userId(),
                clinicId,
                request.specialty(),
                request.documentType(),
                request.documentNumber(),
                request.documentState(),
                request.bio()
        ));
    }

    @Override
    public ProfessionalTenantResponse getProfessionalTenants(@PathVariable UUID userId) throws AuthenticationException {
        return getProfessionalTenantsUseCase.execute(userId);
    }
}

