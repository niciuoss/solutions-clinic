package com.jettech.api.solutions_clinic.model.usecase.patient;

import com.jettech.api.solutions_clinic.model.entity.Patient;
import com.jettech.api.solutions_clinic.model.repository.PatientRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
import java.util.UUID;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultGetPatientByIdUseCase implements GetPatientByIdUseCase {

    private final PatientRepository patientRepository;

    @Override
    @Transactional(readOnly = true)
    public PatientResponse execute(UUID id) throws AuthenticationException {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado com ID: " + id));

        return new PatientResponse(
                patient.getId(),
                patient.getTenant().getId(),
                patient.getFirstName(),
                patient.getCpf(),
                patient.getBirthDate(),
                patient.getGender(),
                patient.getEmail(),
                patient.getPhone(),
                patient.getWhatsapp(),
                patient.getAddressStreet(),
                patient.getAddressNumber(),
                patient.getAddressComplement(),
                patient.getAddressNeighborhood(),
                patient.getAddressCity(),
                patient.getAddressState(),
                patient.getAddressZipcode(),
                patient.getBloodType(),
                patient.getAllergies(),
                patient.getGuardianName(),
                patient.getGuardianPhone(),
                patient.getGuardianRelationship(),
                patient.isActive(),
                patient.getCreatedAt(),
                patient.getUpdatedAt()
        );
    }
}

