package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.model.usecase.room.CreateRoomRequest;
import com.jettech.api.solutions_clinic.model.usecase.room.RoomResponse;
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

import java.util.UUID;

@Tag(name = "Salas", description = "Endpoints para gerenciamento de salas")
public interface RoomAPI {

    @PostMapping("/rooms")
    @Operation(summary = "Cria uma nova sala", description = "Registra uma nova sala no sistema associada a uma clínica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sala criada com sucesso", 
                    content = @Content(schema = @Schema(implementation = RoomResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Clínica não encontrada", content = @Content)
    })
    ResponseEntity<Object> createRoom(@Valid @RequestBody CreateRoomRequest request);

    @GetMapping("/rooms/{id}")
    @Operation(
        summary = "Busca sala por ID",
        description = "Retorna os dados da sala incluindo todas as informações cadastradas."
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200",
                description = "Sala encontrada com sucesso",
                content = @Content(schema = @Schema(implementation = RoomResponse.class))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Sala não encontrada",
                content = @Content
            )
    })
    ResponseEntity<Object> getRoomById(@PathVariable UUID id);
}

