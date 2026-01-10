package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.model.usecase.patient.CreatePatientRequest;
import com.jettech.api.solutions_clinic.model.usecase.patient.DefaultCreatePatientUseCase;
import com.jettech.api.solutions_clinic.model.usecase.patient.DefaultGetPatientByIdUseCase;
import com.jettech.api.solutions_clinic.model.usecase.patient.DefaultGetPatientsByTenantUseCase;
import com.jettech.api.solutions_clinic.model.usecase.patient.GetPatientsByTenantRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class PatientController implements PatientAPI {

    private final DefaultCreatePatientUseCase createPatientUseCase;
    private final DefaultGetPatientByIdUseCase getPatientByIdUseCase;
    private final DefaultGetPatientsByTenantUseCase getPatientsByTenantUseCase;

    @Override
    public ResponseEntity<Object> createPatient(@Valid @RequestBody CreatePatientRequest request) {
        try {
            var result = this.createPatientUseCase.execute(request);
            return ResponseEntity.ok().body(result);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            if (e.getMessage().contains("já existe")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao criar paciente: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Object> getPatientsByTenant(
            @RequestParam UUID tenantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "firstName,asc") String sort) {
        try {
            var request = new GetPatientsByTenantRequest(tenantId, page, size, sort);
            var result = getPatientsByTenantUseCase.execute(request);
            return ResponseEntity.ok().body(result);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrado") || e.getMessage().contains("não encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao listar pacientes: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Object> getPatientById(@PathVariable UUID id) {
        try {
            var result = getPatientByIdUseCase.execute(id);
            return ResponseEntity.ok().body(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar paciente: " + e.getMessage());
        }
    }
}

