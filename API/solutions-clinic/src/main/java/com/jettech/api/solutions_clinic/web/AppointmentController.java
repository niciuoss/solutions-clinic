package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.model.entity.AppointmentStatus;
import com.jettech.api.solutions_clinic.model.usecase.appointment.*;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class AppointmentController implements AppointmentAPI {

    private final DefaultCreateAppointmentUseCase createAppointmentUseCase;
    private final DefaultGetAppointmentByIdUseCase getAppointmentByIdUseCase;
    private final DefaultGetAppointmentsByProfessionalIdUseCase getAppointmentsByProfessionalIdUseCase;
    private final DefaultGetAppointmentsByTenantUseCase getAppointmentsByTenantUseCase;
    private final DefaultUpdateAppointmentUseCase updateAppointmentUseCase;
    private final DefaultDeleteAppointmentUseCase deleteAppointmentUseCase;

    @Override
    public ResponseEntity<Object> createAppointment(@Valid @RequestBody CreateAppointmentRequest request) {
        try {
            var result = this.createAppointmentUseCase.execute(request);
            return ResponseEntity.ok().body(result);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrado") || e.getMessage().contains("não encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            if (e.getMessage().contains("Já existe") || e.getMessage().contains("horário indisponível") || 
                e.getMessage().contains("fora do horário") || e.getMessage().contains("intervalo de almoço")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao criar agendamento: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Object> getAppointmentById(@PathVariable UUID id) {
        try {
            var result = this.getAppointmentByIdUseCase.execute(id);
            return ResponseEntity.ok().body(result);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrado") || e.getMessage().contains("não encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar agendamento: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Object> getAppointmentsByProfessionalId(@PathVariable UUID professionalId) {
        try {
            var result = this.getAppointmentsByProfessionalIdUseCase.execute(professionalId);
            return ResponseEntity.ok().body(result);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar agendamentos: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Object> updateAppointment(@Valid @RequestBody UpdateAppointmentRequest request) {
        try {
            var result = this.updateAppointmentUseCase.execute(request);
            return ResponseEntity.ok().body(result);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrado") || e.getMessage().contains("não encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            if (e.getMessage().contains("Já existe") || e.getMessage().contains("horário indisponível") || 
                e.getMessage().contains("fora do horário") || e.getMessage().contains("intervalo de almoço") ||
                e.getMessage().contains("Não é possível atualizar")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar agendamento: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Object> deleteAppointment(@PathVariable UUID id) {
        try {
            this.deleteAppointmentUseCase.execute(id);
            return ResponseEntity.ok().body("Agendamento cancelado com sucesso");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrado") || e.getMessage().contains("não encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao cancelar agendamento: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Object> getAppointmentsByTenant(
            @PathVariable UUID tenantId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false, defaultValue = "scheduledAt_desc") String orderBy) {
        try {
            var request = new GetAppointmentsByTenantRequest(tenantId, date, status, orderBy);
            var result = this.getAppointmentsByTenantUseCase.execute(request);
            return ResponseEntity.ok().body(result);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrado") || e.getMessage().contains("não encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar agendamentos: " + e.getMessage());
        }
    }
}
