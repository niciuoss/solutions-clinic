package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.model.usecase.professional.AddProfessionalToClinicBodyRequest;
import com.jettech.api.solutions_clinic.model.usecase.professional.CreateProfessionalRequest;
import com.jettech.api.solutions_clinic.model.usecase.professional.ProfessionalResponse;
import com.jettech.api.solutions_clinic.model.usecase.professional.ProfessionalTenantResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Tag(name = "Profissionais", description = "Endpoints para gerenciamento de profissionais")
public interface ProfessionalAPI {

    @PostMapping("/professionals")
    @Operation(summary = "Cria um novo profissional", description = "Registra um novo profissional no sistema associado a um usuário e uma clínica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profissional criado com sucesso", 
                    content = @Content(schema = @Schema(implementation = ProfessionalResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário ou clínica não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Profissional já existe para este usuário e clínica", content = @Content)
    })
    ResponseEntity<Object> createProfessional(@Valid @RequestBody CreateProfessionalRequest request);

    @PostMapping("/clinics/{clinicId}/professionals")
    @Operation(summary = "Adiciona profissional a uma clínica", description = "Registra um profissional associando-o a uma clínica específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profissional adicionado à clínica com sucesso", 
                    content = @Content(schema = @Schema(implementation = ProfessionalResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário ou clínica não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Profissional já existe para este usuário e clínica", content = @Content)
    })
    ResponseEntity<Object> addProfessionalToClinic(
            @PathVariable UUID clinicId,
            @Valid @RequestBody AddProfessionalToClinicBodyRequest request
    );

    @GetMapping("/professionals/{userId}/tenants")
    @Operation(summary = "Lista tenants vinculados a um profissional", description = "Retorna todos os tenants (clínicas) onde o usuário atua como profissional.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tenants retornada com sucesso", 
                    content = @Content(schema = @Schema(implementation = ProfessionalTenantResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    ResponseEntity<Object> getProfessionalTenants(@PathVariable UUID userId);
}

