package com.jettech.api.solutions_clinic.model.usecase.medicalrecord;

import com.jettech.api.solutions_clinic.model.entity.Appointment;
import com.jettech.api.solutions_clinic.model.entity.MedicalRecord;
import com.jettech.api.solutions_clinic.model.entity.MedicalRecordTemplate;
import com.jettech.api.solutions_clinic.model.repository.AppointmentRepository;
import com.jettech.api.solutions_clinic.model.repository.MedicalRecordRepository;
import com.jettech.api.solutions_clinic.model.repository.MedicalRecordTemplateRepository;
import com.jettech.api.solutions_clinic.security.TenantContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jettech.api.solutions_clinic.exception.AuthenticationFailedException;
import com.jettech.api.solutions_clinic.exception.EntityNotFoundException;
import com.jettech.api.solutions_clinic.exception.ForbiddenException;

import java.util.UUID;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultCreateOrUpdateMedicalRecordUseCase implements CreateOrUpdateMedicalRecordUseCase {

    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordTemplateRepository templateRepository;
    private final AppointmentRepository appointmentRepository;
    private final TenantContext tenantContext;

    @Override
    @Transactional
    public MedicalRecordResponse execute(CreateOrUpdateMedicalRecordRequest request) throws AuthenticationFailedException {
        UUID tenantId = tenantContext.getRequiredClinicId();

        Appointment appointment = appointmentRepository.findById(request.appointmentId())
                .orElseThrow(() -> new EntityNotFoundException("Agendamento", request.appointmentId()));
        if (!appointment.getTenant().getId().equals(tenantId)) {
            throw new ForbiddenException(com.jettech.api.solutions_clinic.exception.ApiError.ACCESS_DENIED);
        }

        // Template pode ser global (tenant_id IS NULL) ou da clínica
        MedicalRecordTemplate template = templateRepository.findByIdAvailableForTenant(request.templateId(), tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Modelo de prontuário", request.templateId()));

        MedicalRecord record = medicalRecordRepository.findByAppointmentId(request.appointmentId())
                .orElseGet(() -> {
                    MedicalRecord newRecord = new MedicalRecord();
                    newRecord.setAppointment(appointment);
                    newRecord.setTemplate(template);
                    return newRecord;
                });

        record.setTemplate(template);
        record.setContent(request.content());
        record.setVitalSigns(request.vitalSigns());

        record = medicalRecordRepository.save(record);
        return toResponse(record);
    }

    static MedicalRecordResponse toResponse(MedicalRecord record) {
        return new MedicalRecordResponse(
                record.getId(),
                record.getAppointment().getId(),
                record.getTemplate().getId(),
                record.getContent(),
                record.getVitalSigns(),
                record.getSignedAt(),
                record.getCreatedAt(),
                record.getUpdatedAt()
        );
    }
}
