package com.jettech.api.solutions_clinic.model.usecase.professionalschedule;

import com.jettech.api.solutions_clinic.model.entity.ProfessionalSchedule;
import com.jettech.api.solutions_clinic.model.repository.ProfessionalScheduleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jettech.api.solutions_clinic.exception.AuthenticationFailedException;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultUpdateProfessionalScheduleUseCase implements UpdateProfessionalScheduleUseCase {

    private final ProfessionalScheduleRepository professionalScheduleRepository;

    @Override
    @Transactional
    public ProfessionalScheduleResponse execute(UpdateProfessionalScheduleRequest request) throws AuthenticationFailedException {
        ProfessionalSchedule schedule = professionalScheduleRepository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("Agenda não encontrada com ID: " + request.id()));

        // Validar horários
        validateTimeRange(request.startTime(), request.endTime(), request.lunchBreakStart(), request.lunchBreakEnd());

        // Atualizar campos
        schedule.setStartTime(request.startTime());
        schedule.setEndTime(request.endTime());
        schedule.setLunchBreakStart(request.lunchBreakStart());
        schedule.setLunchBreakEnd(request.lunchBreakEnd());
        schedule.setSlotDurationMinutes(request.slotDurationMinutes());

        schedule = professionalScheduleRepository.save(schedule);

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

    private void validateTimeRange(java.time.LocalTime startTime, java.time.LocalTime endTime,
                                   java.time.LocalTime lunchBreakStart, java.time.LocalTime lunchBreakEnd) {
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new RuntimeException("O horário de início deve ser anterior ao horário de término");
        }

        if (lunchBreakStart.isAfter(lunchBreakEnd) || lunchBreakStart.equals(lunchBreakEnd)) {
            throw new RuntimeException("O horário de início do almoço deve ser anterior ao horário de término");
        }

        if (lunchBreakStart.isBefore(startTime) || lunchBreakEnd.isAfter(endTime)) {
            throw new RuntimeException("O horário de almoço deve estar dentro do horário de trabalho");
        }
    }
}
