package com.jettech.api.solutions_clinic.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jettech.api.solutions_clinic.model.entity.AppointmentStatus;
import com.jettech.api.solutions_clinic.model.usecase.appointment.*;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import com.jettech.api.solutions_clinic.exception.AuthenticationFailedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class AppointmentController implements AppointmentAPI {

    private final CreateAppointmentUseCase createAppointmentUseCase;
    private final GetAppointmentByIdUseCase getAppointmentByIdUseCase;
    private final GetAppointmentsByProfessionalIdUseCase getAppointmentsByProfessionalIdUseCase;
    private final GetAppointmentsByTenantUseCase getAppointmentsByTenantUseCase;
    private final UpdateAppointmentUseCase updateAppointmentUseCase;
    private final DeleteAppointmentUseCase deleteAppointmentUseCase;
    private final CheckAvailabilityUseCase checkAvailabilityUseCase;
    private final GetAvailableSlotsUseCase getAvailableSlotsUseCase;
    private final SaveTriageUseCase saveTriageUseCase;

    @Override
    public AppointmentResponse createAppointment(@Valid @RequestBody CreateAppointmentRequest request) throws AuthenticationFailedException {
        return createAppointmentUseCase.execute(request);
    }

    @Override
    public AppointmentResponse getAppointmentById(@PathVariable UUID id) throws AuthenticationFailedException {
        return getAppointmentByIdUseCase.execute(id);
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByProfessionalId(@PathVariable UUID professionalId) throws AuthenticationFailedException {
        return getAppointmentsByProfessionalIdUseCase.execute(professionalId);
    }

    @Override
    public AppointmentResponse updateAppointment(@Valid @RequestBody UpdateAppointmentRequest request) throws AuthenticationFailedException {
        return updateAppointmentUseCase.execute(request);
    }

    @Override
    public void deleteAppointment(@PathVariable UUID id) throws AuthenticationFailedException {
        deleteAppointmentUseCase.execute(id);
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByTenant(
            @PathVariable UUID tenantId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false, defaultValue = "scheduledAt_desc") String orderBy) throws AuthenticationFailedException {
        return getAppointmentsByTenantUseCase.execute(new GetAppointmentsByTenantRequest(tenantId, date, startDate, endDate, status, orderBy));
    }

    @Override
    public Boolean checkAvailability(
            @RequestParam UUID professionalId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam int durationMinutes,
            @RequestParam(required = false) UUID appointmentId) throws AuthenticationFailedException {
        return checkAvailabilityUseCase.execute(new CheckAvailabilityRequest(professionalId, startTime, durationMinutes, appointmentId));
    }

    @Override
    public List<String> getAvailableSlots(
            @RequestParam UUID professionalId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "60") int durationMinutes) throws AuthenticationFailedException {
        return getAvailableSlotsUseCase.execute(new GetAvailableSlotsRequest(professionalId, date, durationMinutes));
    }

    @Override
    public AppointmentResponse saveTriage(@PathVariable UUID id, @RequestBody Map<String, Object> vitalSigns) throws AuthenticationFailedException {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        vitalSigns.forEach((key, value) -> {
            if (value instanceof String s) node.put(key, s);
            else if (value instanceof Integer i) node.put(key, i);
            else if (value instanceof Long l) node.put(key, l);
            else if (value instanceof Double d) node.put(key, d);
            else if (value instanceof Boolean b) node.put(key, b);
            else if (value instanceof Number n) node.put(key, n.doubleValue());
            else if (value != null) node.put(key, value.toString());
        });
        return saveTriageUseCase.execute(new SaveTriageRequest(id, node));
    }
}
