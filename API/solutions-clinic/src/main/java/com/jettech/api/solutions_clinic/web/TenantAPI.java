package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.model.usecase.tenant.TenantResponse;
import com.jettech.api.solutions_clinic.model.usecase.tenant.UpdateTenantPlanBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.naming.AuthenticationException;
import java.util.UUID;

@Tag(name = "Tenants", description = "Endpoints para gerenciamento de tenants (clínicas)")
public interface TenantAPI {

    @PatchMapping("/tenants/{tenantId}/plan")
    @Operation(
        summary = "Atualiza o plano de um tenant",
        description = "Atualiza o plano selecionado pelo tenant. Se o tenant estava em PENDING_SETUP e seleciona um plano BASIC ou PRO, o status é atualizado para ACTIVE."
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200",
                description = "Plano atualizado com sucesso",
                content = @Content(schema = @Schema(implementation = TenantResponse.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Dados inválidos",
                content = @Content
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Tenant não encontrado",
                content = @Content
            )
    })
    TenantResponse updateTenantPlan(
            @PathVariable UUID tenantId,
            @Valid @RequestBody UpdateTenantPlanBody body
    ) throws AuthenticationException;
}
