package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.model.usecase.subscription.CreateCheckoutSessionBody;
import com.jettech.api.solutions_clinic.model.usecase.subscription.CreateCheckoutSessionResponse;
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
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/tenants/{tenantId}/checkout")
    @Operation(
        summary = "Cria sessão de checkout do Stripe",
        description = "Cria uma sessão de checkout do Stripe para pagamento do plano selecionado pelo tenant"
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200",
                description = "Sessão de checkout criada com sucesso",
                content = @Content(schema = @Schema(implementation = CreateCheckoutSessionResponse.class))
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
    CreateCheckoutSessionResponse createCheckoutSession(
            @PathVariable UUID tenantId,
            @Valid @RequestBody CreateCheckoutSessionBody body
    ) throws AuthenticationException;

    @PostMapping("/tenants/{tenantId}/users/{userId}/roles/{role}")
    @Operation(
        summary = "Associa um usuário a uma clínica com um papel específico",
        description = "Cria uma associação entre um usuário e uma clínica (tenant) com um papel (role) específico. " +
                      "Se a associação já existir, retorna erro."
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200",
                description = "Usuário associado à clínica com sucesso",
                content = @Content
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Dados inválidos ou associação já existe",
                content = @Content
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Usuário ou clínica não encontrado",
                content = @Content
            )
    })
    void associateUserToTenant(
            @PathVariable UUID tenantId,
            @PathVariable UUID userId,
            @PathVariable String role
    ) throws AuthenticationException;
}
