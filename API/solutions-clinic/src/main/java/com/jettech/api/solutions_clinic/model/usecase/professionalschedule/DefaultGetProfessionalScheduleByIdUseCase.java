package com.jettech.api.solutions_clinic.model.usecase.professionalschedule;

import com.jettech.api.solutions_clinic.model.entity.ProfessionalSchedule;
import com.jettech.api.solutions_clinic.model.repository.ProfessionalScheduleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.jettech.api.solutions_clinic.exception.AuthenticationFailedException;
import com.jettech.api.solutions_clinic.exception.EntityNotFoundException;
import java.util.UUID;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultGetProfessionalScheduleByIdUseCase implements GetProfessionalScheduleByIdUseCase {

    private final ProfessionalScheduleRepository professionalScheduleRepository;

    @Override
    public ProfessionalScheduleResponse execute(UUID id) throws AuthenticationFailedException {
        ProfessionalSchedule schedule = professionalScheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agenda", id));

        return new ProfessionalScheduleResponse(
                schedule.getId(),
                schedule.getProfessional().getId(),
                schedule.getDayOfWeek(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getLunchBreakStart(),
                schedule.getLunchBreakEnd(),
                schedule.getSlotDurationMinutes(),
                schedule.getCreatedAt(),
                schedule.getUpdatedAt()
        );
    }
}
