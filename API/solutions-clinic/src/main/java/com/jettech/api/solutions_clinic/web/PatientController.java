package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.model.usecase.patient.CreatePatientRequest;
import com.jettech.api.solutions_clinic.model.usecase.patient.DefaultCreatePatientUseCase;
import com.jettech.api.solutions_clinic.model.usecase.patient.DefaultGetPatientByIdUseCase;
import com.jettech.api.solutions_clinic.model.usecase.patient.DefaultGetPatientsByTenantUseCase;
import com.jettech.api.solutions_clinic.model.usecase.patient.GetPatientsByTenantRequest;
import com.jettech.api.solutions_clinic.model.usecase.patient.PatientResponse;
import com.jettech.api.solutions_clinic.model.usecase.patient.UpdatePatientActiveBodyRequest;
import com.jettech.api.solutions_clinic.model.usecase.patient.UpdatePatientActiveRequest;
import com.jettech.api.solutions_clinic.model.usecase.patient.UpdatePatientActiveUseCase;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class PatientController implements PatientAPI {

    private final DefaultCreatePatientUseCase createPatientUseCase;
    private final DefaultGetPatientByIdUseCase getPatientByIdUseCase;
    private final DefaultGetPatientsByTenantUseCase getPatientsByTenantUseCase;
    private final UpdatePatientActiveUseCase updatePatientActiveUseCase;

    @Override
    public PatientResponse createPatient(@Valid @RequestBody CreatePatientRequest request) throws AuthenticationException {
        return createPatientUseCase.execute(request);
    }

    @Override
    public Page<PatientResponse> getPatientsByTenant(
            @RequestParam UUID tenantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "firstName,asc") String sort,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active) throws AuthenticationException {
        return getPatientsByTenantUseCase.execute(new GetPatientsByTenantRequest(tenantId, page, size, sort, search, active));
    }

    @Override
    public PatientResponse getPatientById(@PathVariable UUID id) throws AuthenticationException {
        return getPatientByIdUseCase.execute(id);
    }

    @Override
    public PatientResponse updatePatientActive(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePatientActiveBodyRequest request) throws AuthenticationException {
        return updatePatientActiveUseCase.execute(new UpdatePatientActiveRequest(id, request.active()));
    }
}

