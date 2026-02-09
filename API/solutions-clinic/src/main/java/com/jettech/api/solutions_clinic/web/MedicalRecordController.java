package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.model.usecase.medicalrecord.CreateOrUpdateMedicalRecordRequest;
import com.jettech.api.solutions_clinic.model.usecase.medicalrecord.GetMedicalRecordByAppointmentUseCase;
import com.jettech.api.solutions_clinic.model.usecase.medicalrecord.GetMedicalRecordByIdUseCase;
import com.jettech.api.solutions_clinic.model.usecase.medicalrecord.MedicalRecordResponse;
import com.jettech.api.solutions_clinic.model.usecase.medicalrecord.CreateOrUpdateMedicalRecordUseCase;
import com.jettech.api.solutions_clinic.model.usecase.medicalrecord.SignMedicalRecordUseCase;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jettech.api.solutions_clinic.exception.AuthenticationFailedException;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class MedicalRecordController implements MedicalRecordAPI {

    private final CreateOrUpdateMedicalRecordUseCase createOrUpdateUseCase;
    private final GetMedicalRecordByAppointmentUseCase getByAppointmentUseCase;
    private final GetMedicalRecordByIdUseCase getByIdUseCase;
    private final SignMedicalRecordUseCase signUseCase;

    @Override
    public MedicalRecordResponse createOrUpdate(@Valid @RequestBody CreateOrUpdateMedicalRecordRequest request) throws AuthenticationFailedException {
        return createOrUpdateUseCase.execute(request);
    }

    @Override
    public ResponseEntity<MedicalRecordResponse> getByAppointment(@PathVariable UUID appointmentId) throws AuthenticationFailedException {
        return getByAppointmentUseCase.execute(appointmentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @Override
    public MedicalRecordResponse getById(@PathVariable UUID id) throws AuthenticationFailedException {
        return getByIdUseCase.execute(id);
    }

    @Override
    public MedicalRecordResponse sign(@PathVariable UUID id) throws AuthenticationFailedException {
        return signUseCase.execute(id);
    }
}
