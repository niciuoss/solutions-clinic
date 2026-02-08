package com.jettech.api.solutions_clinic.model.usecase.professionalschedule;

import com.jettech.api.solutions_clinic.model.entity.ProfessionalSchedule;
import com.jettech.api.solutions_clinic.model.repository.ProfessionalRepository;
import com.jettech.api.solutions_clinic.model.repository.ProfessionalScheduleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.jettech.api.solutions_clinic.exception.AuthenticationFailedException;
import com.jettech.api.solutions_clinic.exception.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultGetProfessionalSchedulesByProfessionalIdUseCase implements GetProfessionalSchedulesByProfessionalIdUseCase {

    private final ProfessionalScheduleRepository professionalScheduleRepository;
    private final ProfessionalRepository professionalRepository;

    @Override
    public List<ProfessionalScheduleResponse> execute(UUID professionalId) throws AuthenticationFailedException {
        // Validar se o profissional existe
        professionalRepository.findById(professionalId)
                .orElseThrow(() -> new EntityNotFoundException("Profissional", professionalId));

        List<ProfessionalSchedule> schedules = professionalScheduleRepository.findByProfessionalId(professionalId);

        return schedules.stream()
                .map(schedule -> new ProfessionalScheduleResponse(
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
                ))
                .collect(Collectors.toList());
    }
}
