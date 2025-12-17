package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.model.entity.User;
import com.jettech.api.solutions_clinic.model.usecase.user.CreateUserRequest;
import com.jettech.api.solutions_clinic.model.usecase.user.UserDetailResponse;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
@Tag(name = "Usuários", description = "Endpoints para gerenciamento de usuários")
public interface UserAPI {

    @PostMapping
    @Operation(summary = "Cria um novo usuário", description = "Registra um novo usuário no sistema com o papel padrão de 'USER'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário criado com sucesso", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "409", description = "Email já existente", content = @Content)
    })
    ResponseEntity<Object> createUser(@Valid @RequestBody CreateUserRequest user);

    @GetMapping("/{id}")
    @Operation(
        summary = "Busca usuário por ID",
        description = "Retorna os dados do usuário incluindo seus tenants e roles associados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200",
                description = "Usuário encontrado com sucesso",
                content = @Content(schema = @Schema(implementation = UserDetailResponse.class))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Usuário não encontrado",
                content = @Content
            )
    })
    ResponseEntity<Object> getUserById(@PathVariable UUID id);
}

