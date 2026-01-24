package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.model.usecase.patient.CreatePatientRequest;
import com.jettech.api.solutions_clinic.model.usecase.patient.PatientResponse;
import com.jettech.api.solutions_clinic.model.usecase.patient.UpdatePatientActiveBodyRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.naming.AuthenticationException;
import java.util.UUID;

@Tag(name = "Pacientes", description = "Endpoints para gerenciamento de pacientes")
public interface PatientAPI {

    @PostMapping("/patients")
    @Operation(summary = "Cria um novo paciente", description = "Registra um novo paciente no sistema associado a uma clínica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paciente criado com sucesso", 
                    content = @Content(schema = @Schema(implementation = PatientResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Clínica não encontrada", content = @Content),
            @ApiResponse(responseCode = "409", description = "Paciente já existe com este CPF nesta clínica", content = @Content)
    })
    PatientResponse createPatient(@Valid @RequestBody CreatePatientRequest request) throws AuthenticationException;

    @GetMapping("/patients")
    @Operation(
        summary = "Lista pacientes de uma clínica com paginação, busca e filtros",
        description = "Retorna uma lista paginada de pacientes de uma clínica (tenant). Suporta busca por nome, CPF, telefone ou email e filtro por status (ativo/inativo)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200",
                description = "Lista de pacientes retornada com sucesso",
                content = @Content(schema = @Schema(implementation = Page.class))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Clínica não encontrada",
                content = @Content
            )
    })
    Page<PatientResponse> getPatientsByTenant(
            @RequestParam UUID tenantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "firstName,asc") String sort,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active
    ) throws AuthenticationException;

    @GetMapping("/patients/{id}")
    @Operation(
        summary = "Busca paciente por ID",
        description = "Retorna os dados do paciente incluindo todas as informações cadastradas."
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200",
                description = "Paciente encontrado com sucesso",
                content = @Content(schema = @Schema(implementation = PatientResponse.class))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Paciente não encontrado",
                content = @Content
            )
    })
    PatientResponse getPatientById(@PathVariable UUID id) throws AuthenticationException;

    @PatchMapping("/patients/{id}/active")
    @Operation(summary = "Atualiza o status ativo de um paciente", description = "Ativa ou desativa um paciente no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status do paciente atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = PatientResponse.class))),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado", content = @Content)
    })
    PatientResponse updatePatientActive(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePatientActiveBodyRequest request
    ) throws AuthenticationException;
}

