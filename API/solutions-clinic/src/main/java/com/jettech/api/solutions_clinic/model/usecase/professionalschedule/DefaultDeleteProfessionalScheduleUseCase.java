package com.jettech.api.solutions_clinic.model.usecase.professionalschedule;

import com.jettech.api.solutions_clinic.model.entity.ProfessionalSchedule;
import com.jettech.api.solutions_clinic.model.repository.ProfessionalScheduleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultDeleteProfessionalScheduleUseCase implements DeleteProfessionalScheduleUseCase {

    private final ProfessionalScheduleRepository professionalScheduleRepository;

    @Override
    @Transactional
    public void execute(UUID id) {
        ProfessionalSchedule schedule = professionalScheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agenda não encontrada com ID: " + id));

        professionalScheduleRepository.delete(schedule);
    }
}
