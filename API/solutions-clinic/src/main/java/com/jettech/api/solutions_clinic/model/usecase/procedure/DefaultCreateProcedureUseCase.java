package com.jettech.api.solutions_clinic.model.usecase.procedure;

import com.jettech.api.solutions_clinic.model.entity.Procedure;
import com.jettech.api.solutions_clinic.model.entity.Tenant;
import com.jettech.api.solutions_clinic.model.repository.ProcedureRepository;
import com.jettech.api.solutions_clinic.model.repository.TenantRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultCreateProcedureUseCase implements CreateProcedureUseCase {

    private final ProcedureRepository procedureRepository;
    private final TenantRepository tenantRepository;

    @Override
    @Transactional
    public ProcedureResponse execute(CreateProcedureRequest request) throws AuthenticationException {
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new RuntimeException("Clínica não encontrada com ID: " + request.tenantId()));

        Procedure procedure = new Procedure();
        procedure.setTenant(tenant);
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
}
