package com.jettech.api.solutions_clinic.model.usecase.procedure;

import com.jettech.api.solutions_clinic.model.entity.Professional;
import com.jettech.api.solutions_clinic.model.entity.Procedure;
import com.jettech.api.solutions_clinic.model.entity.Tenant;
import com.jettech.api.solutions_clinic.model.repository.ProcedureRepository;
import com.jettech.api.solutions_clinic.model.repository.ProfessionalRepository;
import com.jettech.api.solutions_clinic.model.repository.TenantRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
import java.util.UUID;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultCreateProcedureUseCase implements CreateProcedureUseCase {

    private final ProcedureRepository procedureRepository;
    private final TenantRepository tenantRepository;
    private final ProfessionalRepository professionalRepository;

    @Override
    @Transactional
    public ProcedureResponse execute(CreateProcedureRequest request) throws AuthenticationException {
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new RuntimeException("Clínica não encontrada com ID: " + request.tenantId()));
                
        // Obter usuário logado
        UUID userId = getUserIdFromContext();
        if (userId == null && request.professionalId() == null) {
            throw new AuthenticationException("Usuário não identificado e profissional não informado");
        }
        
        Professional professional;
        if (request.professionalId() != null) {
            // Se foi informado o ID do profissional, busca diretamente (por exemplo, admin criando para um médico)
            professional = professionalRepository.findByIdAndTenantId(request.professionalId(), tenant.getId())
                    .orElseThrow(() -> new RuntimeException("Profissional não encontrado com ID: " + request.professionalId()));
        } else {
             // Buscar profissional associado ao usuário logado e tenant
            professional = professionalRepository.findByUserIdAndTenantId(userId, tenant.getId())
                    .orElseThrow(() -> new RuntimeException("Profissional não encontrado para o usuário atual nesta clínica. Apenas profissionais podem criar procedimentos ou é necessário informar o ID do profissional."));
        }

        Procedure procedure = new Procedure();
        procedure.setTenant(tenant);
        procedure.setProfessional(professional);
        procedure.setName(request.name());
        procedure.setDescription(request.description());
        procedure.setEstimatedDurationMinutes(request.estimatedDurationMinutes());
        procedure.setBasePrice(request.basePrice());
        procedure.setProfessionalCommissionPercent(request.professionalCommissionPercent());
        procedure.setActive(true);

        procedure = procedureRepository.save(procedure);

        return new ProcedureResponse(
                procedure.getId(),
                procedure.getTenant().getId(),
                procedure.getName(),
                procedure.getDescription(),
                procedure.getEstimatedDurationMinutes(),
                procedure.getBasePrice(),
                procedure.getProfessionalCommissionPercent(),
                procedure.isActive(),
                procedure.getCreatedAt(),
                procedure.getUpdatedAt()
        );
    }
    
    private UUID getUserIdFromContext() {
        try {
            org.springframework.security.core.Authentication authentication = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.oauth2.jwt.Jwt) {
                org.springframework.security.oauth2.jwt.Jwt jwt = 
                    (org.springframework.security.oauth2.jwt.Jwt) authentication.getPrincipal(); // sub é o userId
                String subject = jwt.getSubject();
                if (subject != null) {
                    return UUID.fromString(subject);
                }
            }
        } catch (Exception e) {
            // Se não conseguir obter, retorna null
        }
        return null;
    }
}
