package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.model.usecase.medicalrecordtemplate.CreateMedicalRecordTemplateRequest;
import com.jettech.api.solutions_clinic.model.usecase.medicalrecordtemplate.GetMedicalRecordTemplatesByTenantRequest;
import com.jettech.api.solutions_clinic.model.usecase.medicalrecordtemplate.MedicalRecordTemplateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.jettech.api.solutions_clinic.exception.AuthenticationFailedException;
import java.util.List;
import java.util.UUID;

@Tag(name = "Modelos de prontuário", description = "Definição de templates (schema) por especialidade")
public interface MedicalRecordTemplateAPI {

    @PostMapping("/medical-record-templates")
    @Operation(summary = "Cria modelo de prontuário", description = "Registra um novo modelo com nome, tipo de profissional e schema JSON dos campos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Modelo criado",
                    content = @Content(schema = @Schema(implementation = MedicalRecordTemplateResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    MedicalRecordTemplateResponse createTemplate(@Valid @RequestBody CreateMedicalRecordTemplateRequest request) throws AuthenticationFailedException;

    @GetMapping("/medical-record-templates/{id}")
    @Operation(summary = "Busca modelo por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Modelo encontrado",
                    content = @Content(schema = @Schema(implementation = MedicalRecordTemplateResponse.class))),
            @ApiResponse(responseCode = "404", description = "Modelo não encontrado", content = @Content)
    })
    MedicalRecordTemplateResponse getTemplateById(@PathVariable UUID id) throws AuthenticationFailedException;

    @GetMapping("/medical-record-templates")
    @Operation(summary = "Lista modelos da clínica", description = "Filtra por tenant, opcionalmente por activeOnly e professionalType.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de modelos",
                    content = @Content(schema = @Schema(implementation = MedicalRecordTemplateResponse.class)))
    })
    List<MedicalRecordTemplateResponse> getTemplatesByTenant(
            @RequestParam UUID tenantId,
            @RequestParam(required = false, defaultValue = "true") boolean activeOnly,
            @RequestParam(required = false) String professionalType
    ) throws AuthenticationFailedException;
}
