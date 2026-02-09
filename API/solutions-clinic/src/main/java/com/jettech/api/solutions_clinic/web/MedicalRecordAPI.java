package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.model.usecase.medicalrecord.CreateOrUpdateMedicalRecordRequest;
import com.jettech.api.solutions_clinic.model.usecase.medicalrecord.MedicalRecordResponse;
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

import com.jettech.api.solutions_clinic.exception.AuthenticationFailedException;
import java.util.UUID;

@Tag(name = "Prontuários", description = "Prontuários por consulta (conteúdo JSON conforme template)")
public interface MedicalRecordAPI {

    @PostMapping("/medical-records")
    @Operation(summary = "Cria ou atualiza prontuário", description = "Por appointmentId. Se já existir, atualiza content e vitalSigns.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prontuário salvo",
                    content = @Content(schema = @Schema(implementation = MedicalRecordResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Agendamento ou modelo não encontrado", content = @Content)
    })
    MedicalRecordResponse createOrUpdate(@Valid @RequestBody CreateOrUpdateMedicalRecordRequest request) throws AuthenticationFailedException;

    @GetMapping("/medical-records/appointment/{appointmentId}")
    @Operation(summary = "Busca prontuário por agendamento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prontuário encontrado ou vazio",
                    content = @Content(schema = @Schema(implementation = MedicalRecordResponse.class))),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", content = @Content)
    })
    ResponseEntity<MedicalRecordResponse> getByAppointment(@PathVariable UUID appointmentId) throws AuthenticationFailedException;

    @GetMapping("/medical-records/{id}")
    @Operation(summary = "Busca prontuário por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prontuário encontrado",
                    content = @Content(schema = @Schema(implementation = MedicalRecordResponse.class))),
            @ApiResponse(responseCode = "404", description = "Prontuário não encontrado", content = @Content)
    })
    MedicalRecordResponse getById(@PathVariable UUID id) throws AuthenticationFailedException;

    @PostMapping("/medical-records/{id}/sign")
    @Operation(summary = "Assina prontuário", description = "Define signed_at com a data/hora atual.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prontuário assinado",
                    content = @Content(schema = @Schema(implementation = MedicalRecordResponse.class))),
            @ApiResponse(responseCode = "404", description = "Prontuário não encontrado", content = @Content)
    })
    MedicalRecordResponse sign(@PathVariable UUID id) throws AuthenticationFailedException;
}
