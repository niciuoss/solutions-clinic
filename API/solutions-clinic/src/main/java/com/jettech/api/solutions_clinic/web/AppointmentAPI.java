package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.model.entity.AppointmentStatus;
import com.jettech.api.solutions_clinic.model.usecase.appointment.AppointmentResponse;
import com.jettech.api.solutions_clinic.model.usecase.appointment.CreateAppointmentRequest;
import com.jettech.api.solutions_clinic.model.usecase.appointment.UpdateAppointmentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@Tag(name = "Agendamentos", description = "Endpoints para gerenciamento de agendamentos")
public interface AppointmentAPI {

    @PostMapping("/appointments")
    @Operation(summary = "Cria um novo agendamento", description = "Registra um novo agendamento após validar horário disponível do profissional e sala.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento criado com sucesso", 
                    content = @Content(schema = @Schema(implementation = AppointmentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou horário indisponível", content = @Content),
            @ApiResponse(responseCode = "404", description = "Paciente, profissional, sala ou usuário não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflito de horário", content = @Content)
    })
    ResponseEntity<Object> createAppointment(@Valid @RequestBody CreateAppointmentRequest request);

    @GetMapping("/appointments/{id}")
    @Operation(summary = "Busca agendamento por ID", description = "Retorna os dados de um agendamento específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento encontrado com sucesso",
                    content = @Content(schema = @Schema(implementation = AppointmentResponse.class))),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", content = @Content)
    })
    ResponseEntity<Object> getAppointmentById(@PathVariable UUID id);

    @GetMapping("/professionals/{professionalId}/appointments")
    @Operation(summary = "Lista agendamentos de um profissional", description = "Retorna todos os agendamentos cadastrados para um profissional.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de agendamentos retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = AppointmentResponse.class))),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado", content = @Content)
    })
    ResponseEntity<Object> getAppointmentsByProfessionalId(@PathVariable UUID professionalId);

    @PutMapping("/appointments")
    @Operation(summary = "Atualiza um agendamento", description = "Atualiza os dados de um agendamento existente. Valida horário disponível se houver mudanças.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = AppointmentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou horário indisponível", content = @Content),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", content = @Content)
    })
    ResponseEntity<Object> updateAppointment(@Valid @RequestBody UpdateAppointmentRequest request);

    @DeleteMapping("/appointments/{id}")
    @Operation(summary = "Cancela um agendamento", description = "Cancela um agendamento (marca como cancelado ao invés de deletar).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento cancelado com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", content = @Content)
    })
    ResponseEntity<Object> deleteAppointment(@PathVariable UUID id);

    @GetMapping("/tenants/{tenantId}/appointments")
    @Operation(summary = "Lista agendamentos de uma clínica", description = "Retorna todos os agendamentos de uma clínica com filtros opcionais de data, status e ordenação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de agendamentos retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = AppointmentResponse.class))),
            @ApiResponse(responseCode = "404", description = "Clínica não encontrada", content = @Content)
    })
    ResponseEntity<Object> getAppointmentsByTenant(
            @PathVariable UUID tenantId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false, defaultValue = "scheduledAt_desc") String orderBy
    );
}
