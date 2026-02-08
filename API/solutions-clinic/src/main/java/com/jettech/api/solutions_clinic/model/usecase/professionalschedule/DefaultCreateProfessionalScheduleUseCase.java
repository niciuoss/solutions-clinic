package com.jettech.api.solutions_clinic.model.usecase.professionalschedule;

import com.jettech.api.solutions_clinic.model.entity.Professional;
import com.jettech.api.solutions_clinic.model.entity.ProfessionalSchedule;
import com.jettech.api.solutions_clinic.model.repository.ProfessionalRepository;
import com.jettech.api.solutions_clinic.model.repository.ProfessionalScheduleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jettech.api.solutions_clinic.exception.ApiError;
import com.jettech.api.solutions_clinic.exception.AuthenticationFailedException;
import com.jettech.api.solutions_clinic.exception.DuplicateEntityException;
import com.jettech.api.solutions_clinic.exception.EntityNotFoundException;
import com.jettech.api.solutions_clinic.exception.ScheduleValidationException;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultCreateProfessionalScheduleUseCase implements CreateProfessionalScheduleUseCase {

    private final ProfessionalScheduleRepository professionalScheduleRepository;
    private final ProfessionalRepository professionalRepository;

    @Override
    @Transactional
    public ProfessionalScheduleResponse execute(CreateProfessionalScheduleRequest request) throws AuthenticationFailedException {
        // Validar se o profissional existe
        Professional professional = professionalRepository.findById(request.professionalId())
                .orElseThrow(() -> new EntityNotFoundException("Profissional", request.professionalId()));

        // Validar se já existe agenda para este profissional neste dia da semana
        professionalScheduleRepository.findByProfessionalIdAndDayOfWeek(
                request.professionalId(), request.dayOfWeek())
                .ifPresent(schedule -> {
                    throw new DuplicateEntityException(ApiError.DUPLICATE_SCHEDULE, request.dayOfWeek());
                });

        // Validar horários
        validateTimeRange(request.startTime(), request.endTime(), request.lunchBreakStart(), request.lunchBreakEnd());

        // Criar ProfessionalSchedule
        ProfessionalSchedule schedule = new ProfessionalSchedule();
        schedule.setProfessional(professional);
        schedule.setDayOfWeek(request.dayOfWeek());
        schedule.setStartTime(request.startTime());
        schedule.setEndTime(request.endTime());
        schedule.setLunchBreakStart(request.lunchBreakStart());
        schedule.setLunchBreakEnd(request.lunchBreakEnd());
        schedule.setSlotDurationMinutes(request.slotDurationMinutes());

        schedule = professionalScheduleRepository.save(schedule);

        // Converter para Response
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
            throw new ScheduleValidationException(ApiError.SCHEDULE_START_BEFORE_END);
        }

        if (lunchBreakStart.isAfter(lunchBreakEnd) || lunchBreakStart.equals(lunchBreakEnd)) {
            throw new ScheduleValidationException(ApiError.SCHEDULE_LUNCH_ORDER);
        }

        if (lunchBreakStart.isBefore(startTime) || lunchBreakEnd.isAfter(endTime)) {
            throw new ScheduleValidationException(ApiError.SCHEDULE_LUNCH_WITHIN_WORK);
        }
    }
}
