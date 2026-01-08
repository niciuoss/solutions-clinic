package com.jettech.api.solutions_clinic.model.usecase.professionalschedule;

import com.jettech.api.solutions_clinic.model.entity.ProfessionalSchedule;
import com.jettech.api.solutions_clinic.model.repository.ProfessionalScheduleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.UUID;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultGetProfessionalScheduleByIdUseCase implements GetProfessionalScheduleByIdUseCase {

    private final ProfessionalScheduleRepository professionalScheduleRepository;

    @Override
    public ProfessionalScheduleResponse execute(UUID id) throws AuthenticationException {
        ProfessionalSchedule schedule = professionalScheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agenda não encontrada com ID: " + id));

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
