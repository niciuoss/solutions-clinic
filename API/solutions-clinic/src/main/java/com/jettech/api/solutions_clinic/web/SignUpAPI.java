package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.model.usecase.signup.SignUpClinicOwnerRequest;
import com.jettech.api.solutions_clinic.model.usecase.signup.SignUpResponse;
import com.jettech.api.solutions_clinic.model.usecase.signup.SignUpSoloRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Validated
@RequestMapping("/v1/auth/signup")
@Tag(name = "Cadastro", description = "Endpoints para cadastro de usuários e tenants")
public interface SignUpAPI {

    @PostMapping("/clinic-owner")
    @Operation(
        summary = "Cadastro de clínica",
        description = "Registra um novo tenant do tipo CLINIC com um usuário OWNER. Requer CNPJ válido."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Cadastro realizado com sucesso",
            content = @Content(schema = @Schema(implementation = SignUpResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos ou já existentes (email, CNPJ ou subdomínio)",
            content = @Content
        )
    })
    ResponseEntity<Object> signUpClinicOwner(@Valid @RequestBody SignUpClinicOwnerRequest request);

    @PostMapping("/solo")
    @Operation(
        summary = "Cadastro de profissional solo",
        description = "Registra um novo tenant do tipo SOLO com um usuário OWNER. Requer CPF válido."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Cadastro realizado com sucesso",
            content = @Content(schema = @Schema(implementation = SignUpResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos ou já existentes (email ou subdomínio)",
            content = @Content
        )
    })
    ResponseEntity<Object> signUpSolo(@Valid @RequestBody SignUpSoloRequest request);
}

