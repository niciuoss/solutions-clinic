package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.model.usecase.professionalschedule.CreateProfessionalScheduleRequest;
import com.jettech.api.solutions_clinic.model.usecase.professionalschedule.ProfessionalScheduleResponse;
import com.jettech.api.solutions_clinic.model.usecase.professionalschedule.UpdateProfessionalScheduleRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import com.jettech.api.solutions_clinic.exception.AuthenticationFailedException;
import java.util.List;
import java.util.UUID;

@Tag(name = "Agendas de Profissionais", description = "Endpoints para gerenciamento de agendas de profissionais")
public interface ProfessionalScheduleAPI {

    @PostMapping("/professional-schedules")
    @Operation(summary = "Cria uma nova agenda para um profissional", description = "Registra uma nova agenda de horários para um profissional em um dia da semana específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agenda criada com sucesso", 
                    content = @Content(schema = @Schema(implementation = ProfessionalScheduleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Já existe uma agenda para este profissional neste dia da semana", content = @Content)
    })
    ProfessionalScheduleResponse createProfessionalSchedule(@Valid @RequestBody CreateProfessionalScheduleRequest request) throws AuthenticationFailedException;

    @GetMapping("/professional-schedules/{id}")
    @Operation(summary = "Busca agenda por ID", description = "Retorna os dados de uma agenda específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agenda encontrada com sucesso",
                    content = @Content(schema = @Schema(implementation = ProfessionalScheduleResponse.class))),
            @ApiResponse(responseCode = "404", description = "Agenda não encontrada", content = @Content)
    })
    ProfessionalScheduleResponse getProfessionalScheduleById(@PathVariable UUID id) throws AuthenticationFailedException;

    @GetMapping("/professionals/{professionalId}/schedules")
    @Operation(summary = "Lista agendas de um profissional", description = "Retorna todas as agendas cadastradas para um profissional.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de agendas retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = ProfessionalScheduleResponse.class))),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado", content = @Content)
    })
    List<ProfessionalScheduleResponse> getProfessionalSchedulesByProfessionalId(@PathVariable UUID professionalId) throws AuthenticationFailedException;

    @PutMapping("/professional-schedules")
    @Operation(summary = "Atualiza uma agenda", description = "Atualiza os dados de uma agenda existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agenda atualizada com sucesso",
                    content = @Content(schema = @Schema(implementation = ProfessionalScheduleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Agenda não encontrada", content = @Content)
    })
    ProfessionalScheduleResponse updateProfessionalSchedule(@Valid @RequestBody UpdateProfessionalScheduleRequest request) throws AuthenticationFailedException;

    @DeleteMapping("/professional-schedules/{id}")
    @Operation(summary = "Deleta uma agenda", description = "Remove uma agenda do sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agenda deletada com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Agenda não encontrada", content = @Content)
    })
    void deleteProfessionalSchedule(@PathVariable UUID id);
}
