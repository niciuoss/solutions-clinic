package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.model.entity.DocumentType;
import com.jettech.api.solutions_clinic.model.usecase.professional.AddProfessionalToClinicBodyRequest;
import com.jettech.api.solutions_clinic.model.usecase.professional.AddProfessionalToClinicRequest;
import com.jettech.api.solutions_clinic.model.usecase.professional.CreateProfessionalRequest;
import com.jettech.api.solutions_clinic.model.usecase.professional.CreateProfessionalWithUserRequest;
import com.jettech.api.solutions_clinic.model.usecase.professional.DefaultAddProfessionalToClinicUseCase;
import com.jettech.api.solutions_clinic.model.usecase.professional.DefaultCreateProfessionalUseCase;
import com.jettech.api.solutions_clinic.model.usecase.professional.DefaultCreateProfessionalWithUserUseCase;
import com.jettech.api.solutions_clinic.model.usecase.professional.DefaultGetProfessionalsByClinicUseCase;
import com.jettech.api.solutions_clinic.model.usecase.professional.DefaultGetProfessionalTenantsUseCase;
import com.jettech.api.solutions_clinic.model.usecase.professional.DefaultUpdateProfessionalActiveUseCase;
import com.jettech.api.solutions_clinic.model.usecase.professional.GetProfessionalsByClinicRequest;
import com.jettech.api.solutions_clinic.model.usecase.professional.ProfessionalResponse;
import com.jettech.api.solutions_clinic.model.usecase.professional.ProfessionalTenantResponse;
import com.jettech.api.solutions_clinic.model.usecase.professional.UpdateProfessionalActiveBodyRequest;
import com.jettech.api.solutions_clinic.model.usecase.professional.UpdateProfessionalActiveRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;
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
    private final DefaultCreateProfessionalWithUserUseCase createProfessionalWithUserUseCase;
    private final DefaultAddProfessionalToClinicUseCase addProfessionalToClinicUseCase;
    private final DefaultGetProfessionalTenantsUseCase getProfessionalTenantsUseCase;
    private final DefaultGetProfessionalsByClinicUseCase getProfessionalsByClinicUseCase;
    private final DefaultUpdateProfessionalActiveUseCase updateProfessionalActiveUseCase;

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

    @Override
    public Page<ProfessionalResponse> getProfessionalsByClinic(
            @PathVariable UUID clinicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "user.fullName,asc") String sort,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String documentType
    ) throws AuthenticationException {
        DocumentType docType = null;
        if (documentType != null && !documentType.isEmpty()) {
            try {
                docType = DocumentType.valueOf(documentType.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Se o tipo não for válido, mantém como null (todos os tipos)
            }
        }
        
        return getProfessionalsByClinicUseCase.execute(new GetProfessionalsByClinicRequest(
                clinicId,
                page,
                size,
                sort,
                search,
                active,
                docType
        ));
    }

    @Override
    public ProfessionalResponse updateProfessionalActive(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProfessionalActiveBodyRequest request) throws AuthenticationException {
        return updateProfessionalActiveUseCase.execute(new UpdateProfessionalActiveRequest(
                id,
                request.active()
        ));
    }

    @Override
    public ProfessionalResponse createProfessionalWithUser(@Valid @RequestBody CreateProfessionalWithUserRequest request) throws AuthenticationException {
        return createProfessionalWithUserUseCase.execute(request);
    }
}

