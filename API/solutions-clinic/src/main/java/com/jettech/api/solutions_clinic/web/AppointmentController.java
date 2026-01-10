package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.model.entity.AppointmentStatus;
import com.jettech.api.solutions_clinic.model.usecase.appointment.*;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class AppointmentController implements AppointmentAPI {

    private final DefaultCreateAppointmentUseCase createAppointmentUseCase;
    private final DefaultGetAppointmentByIdUseCase getAppointmentByIdUseCase;
    private final DefaultGetAppointmentsByProfessionalIdUseCase getAppointmentsByProfessionalIdUseCase;
    private final DefaultGetAppointmentsByTenantUseCase getAppointmentsByTenantUseCase;
    private final DefaultUpdateAppointmentUseCase updateAppointmentUseCase;
    private final DefaultDeleteAppointmentUseCase deleteAppointmentUseCase;

    @Override
    public AppointmentResponse createAppointment(@Valid @RequestBody CreateAppointmentRequest request) throws AuthenticationException {
        return createAppointmentUseCase.execute(request);
    }

    @Override
    public AppointmentResponse getAppointmentById(@PathVariable UUID id) throws AuthenticationException {
        return getAppointmentByIdUseCase.execute(id);
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByProfessionalId(@PathVariable UUID professionalId) throws AuthenticationException {
        return getAppointmentsByProfessionalIdUseCase.execute(professionalId);
    }

    @Override
    public AppointmentResponse updateAppointment(@Valid @RequestBody UpdateAppointmentRequest request) throws AuthenticationException {
        return updateAppointmentUseCase.execute(request);
    }

    @Override
    public void deleteAppointment(@PathVariable UUID id) {
        deleteAppointmentUseCase.execute(id);
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByTenant(
            @PathVariable UUID tenantId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false, defaultValue = "scheduledAt_desc") String orderBy) throws AuthenticationException {
        return getAppointmentsByTenantUseCase.execute(new GetAppointmentsByTenantRequest(tenantId, date, status, orderBy));
    }
}
