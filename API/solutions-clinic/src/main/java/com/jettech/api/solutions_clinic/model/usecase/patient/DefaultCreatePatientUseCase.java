package com.jettech.api.solutions_clinic.model.usecase.patient;

import com.jettech.api.solutions_clinic.model.entity.Patient;
import com.jettech.api.solutions_clinic.model.entity.Tenant;
import com.jettech.api.solutions_clinic.model.repository.PatientRepository;
import com.jettech.api.solutions_clinic.model.repository.TenantRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultCreatePatientUseCase implements CreatePatientUseCase {

    private final PatientRepository patientRepository;
    private final TenantRepository tenantRepository;

    @Override
    @Transactional
    public PatientResponse execute(CreatePatientRequest request) throws AuthenticationException {
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new RuntimeException("Clínica não encontrada com ID: " + request.tenantId()));

        if (request.cpf() != null && !request.cpf().isEmpty()) {
            patientRepository.findByCpfAndTenantId(request.cpf(), request.tenantId())
                    .ifPresent(patient -> {
                        throw new RuntimeException("Paciente já existe com este CPF nesta clínica");
                    });
        }

        Patient patient = new Patient();
        patient.setTenant(tenant);
        patient.setFirstName(request.firstName());
        patient.setCpf(request.cpf());
        patient.setBirthDate(request.birthDate());
        patient.setGender(request.gender());
        patient.setEmail(request.email());
        patient.setPhone(request.phone());
        patient.setWhatsapp(request.whatsapp());
        patient.setAddressStreet(request.addressStreet());
        patient.setAddressNumber(request.addressNumber());
        patient.setAddressComplement(request.addressComplement());
        patient.setAddressNeighborhood(request.addressNeighborhood());
        patient.setAddressCity(request.addressCity());
        patient.setAddressState(request.addressState());
        patient.setAddressZipcode(request.addressZipcode());
        patient.setBloodType(request.bloodType());
        patient.setAllergies(request.allergies());
        patient.setGuardianName(request.guardianName());
        patient.setGuardianPhone(request.guardianPhone());
        patient.setGuardianRelationship(request.guardianRelationship());
        patient.setActive(true);

        patient = patientRepository.save(patient);

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

