package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.model.usecase.professionalschedule.*;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.util.List;
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
    public ProfessionalScheduleResponse createProfessionalSchedule(@Valid @RequestBody CreateProfessionalScheduleRequest request) throws AuthenticationException {
        return createProfessionalScheduleUseCase.execute(request);
    }

    @Override
    public ProfessionalScheduleResponse getProfessionalScheduleById(@PathVariable UUID id) throws AuthenticationException {
        return getProfessionalScheduleByIdUseCase.execute(id);
    }

    @Override
    public List<ProfessionalScheduleResponse> getProfessionalSchedulesByProfessionalId(@PathVariable UUID professionalId) throws AuthenticationException {
        return getProfessionalSchedulesByProfessionalIdUseCase.execute(professionalId);
    }

    @Override
    public ProfessionalScheduleResponse updateProfessionalSchedule(@Valid @RequestBody UpdateProfessionalScheduleRequest request) throws AuthenticationException {
        return updateProfessionalScheduleUseCase.execute(request);
    }

    @Override
    public void deleteProfessionalSchedule(@PathVariable UUID id) {
        deleteProfessionalScheduleUseCase.execute(id);
    }
}
