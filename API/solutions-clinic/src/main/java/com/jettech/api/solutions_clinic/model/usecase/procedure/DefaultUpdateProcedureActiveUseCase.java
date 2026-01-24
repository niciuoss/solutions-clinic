package com.jettech.api.solutions_clinic.model.usecase.procedure;

import com.jettech.api.solutions_clinic.model.entity.Procedure;
import com.jettech.api.solutions_clinic.model.repository.ProcedureRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultUpdateProcedureActiveUseCase implements UpdateProcedureActiveUseCase {

    private final ProcedureRepository procedureRepository;

    @Override
    @Transactional
    public ProcedureResponse execute(UpdateProcedureActiveRequest request) throws AuthenticationException {
        Procedure procedure = procedureRepository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("Procedimento não encontrado com ID: " + request.id()));

        procedure.setActive(request.active());
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
