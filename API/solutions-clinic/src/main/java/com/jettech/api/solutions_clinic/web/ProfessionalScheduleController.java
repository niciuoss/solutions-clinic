package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.model.usecase.professionalschedule.*;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ProfessionalScheduleController implements ProfessionalScheduleAPI {

    private final DefaultCreateProfessionalScheduleUseCase createProfessionalScheduleUseCase;
    private final DefaultGetProfessionalScheduleByIdUseCase getProfessionalScheduleByIdUseCase;
    private final DefaultGetProfessionalSchedulesByProfessionalIdUseCase getProfessionalSchedulesByProfessionalIdUseCase;
    private final DefaultUpdateProfessionalScheduleUseCase updateProfessionalScheduleUseCase;
    private final DefaultDeleteProfessionalScheduleUseCase deleteProfessionalScheduleUseCase;

    @Override
    public ResponseEntity<Object> createProfessionalSchedule(@Valid @RequestBody CreateProfessionalScheduleRequest request) {
        try {
            var result = this.createProfessionalScheduleUseCase.execute(request);
            return ResponseEntity.ok().body(result);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            if (e.getMessage().contains("já existe") || e.getMessage().contains("Já existe")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao criar agenda: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Object> getProfessionalScheduleById(@PathVariable UUID id) {
        try {
            var result = this.getProfessionalScheduleByIdUseCase.execute(id);
            return ResponseEntity.ok().body(result);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrado") || e.getMessage().contains("não encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar agenda: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Object> getProfessionalSchedulesByProfessionalId(@PathVariable UUID professionalId) {
        try {
            var result = this.getProfessionalSchedulesByProfessionalIdUseCase.execute(professionalId);
            return ResponseEntity.ok().body(result);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar agendas: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Object> updateProfessionalSchedule(@Valid @RequestBody UpdateProfessionalScheduleRequest request) {
        try {
            var result = this.updateProfessionalScheduleUseCase.execute(request);
            return ResponseEntity.ok().body(result);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrado") || e.getMessage().contains("não encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar agenda: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Object> deleteProfessionalSchedule(@PathVariable UUID id) {
        try {
            this.deleteProfessionalScheduleUseCase.execute(id);
            return ResponseEntity.ok().body("Agenda deletada com sucesso");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrado") || e.getMessage().contains("não encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao deletar agenda: " + e.getMessage());
        }
    }
}
