package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.model.usecase.professional.AddProfessionalToClinicBodyRequest;
import com.jettech.api.solutions_clinic.model.usecase.professional.AddProfessionalToClinicRequest;
import com.jettech.api.solutions_clinic.model.usecase.professional.CreateProfessionalRequest;
import com.jettech.api.solutions_clinic.model.usecase.professional.DefaultAddProfessionalToClinicUseCase;
import com.jettech.api.solutions_clinic.model.usecase.professional.DefaultCreateProfessionalUseCase;
import com.jettech.api.solutions_clinic.model.usecase.professional.DefaultGetProfessionalTenantsUseCase;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ProfessionalController implements ProfessionalAPI {

    private final DefaultCreateProfessionalUseCase createProfessionalUseCase;
    private final DefaultAddProfessionalToClinicUseCase addProfessionalToClinicUseCase;
    private final DefaultGetProfessionalTenantsUseCase getProfessionalTenantsUseCase;

    @Override
    public ResponseEntity<Object> createProfessional(@Valid @RequestBody CreateProfessionalRequest request) {
        try {
            var result = this.createProfessionalUseCase.execute(request);
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
                    .body("Erro ao criar profissional: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Object> addProfessionalToClinic(
            @PathVariable UUID clinicId,
            @Valid @RequestBody AddProfessionalToClinicBodyRequest request) {
        try {
            // Criar um novo request incluindo o clinicId do path
            var requestWithClinicId = new AddProfessionalToClinicRequest(
                    request.userId(),
                    clinicId,
                    request.specialty(),
                    request.documentType(),
                    request.documentNumber(),
                    request.documentState(),
                    request.bio()
            );
            var result = this.addProfessionalToClinicUseCase.execute(requestWithClinicId);
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
                    .body("Erro ao adicionar profissional à clínica: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Object> getProfessionalTenants(@PathVariable UUID userId) {
        try {
            var result = this.getProfessionalTenantsUseCase.execute(userId);
            return ResponseEntity.ok().body(result);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar tenants do profissional: " + e.getMessage());
        }
    }
}

