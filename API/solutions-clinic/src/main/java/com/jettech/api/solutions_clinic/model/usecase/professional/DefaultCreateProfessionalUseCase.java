package com.jettech.api.solutions_clinic.model.usecase.professional;

import com.jettech.api.solutions_clinic.model.entity.Professional;
import com.jettech.api.solutions_clinic.model.entity.Tenant;
import com.jettech.api.solutions_clinic.model.entity.User;
import com.jettech.api.solutions_clinic.model.repository.ProfessionalRepository;
import com.jettech.api.solutions_clinic.model.repository.TenantRepository;
import com.jettech.api.solutions_clinic.model.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultCreateProfessionalUseCase implements CreateProfessionalUseCase {

    private final ProfessionalRepository professionalRepository;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;

    @Override
    @Transactional
    public ProfessionalResponse execute(CreateProfessionalRequest request) throws AuthenticationException {
        // Validar se o usuário existe
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + request.userId()));

        // Validar se o tenant/clínica existe
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new RuntimeException("Clínica não encontrada com ID: " + request.tenantId()));

        // Validar se já existe profissional com mesmo user e tenant
        professionalRepository.findByUserIdAndTenantId(request.userId(), request.tenantId())
                .ifPresent(professional -> {
                    throw new RuntimeException("Profissional já existe para este usuário e clínica");
                });

        // Criar Professional
        Professional professional = new Professional();
        professional.setUser(user);
        professional.setTenant(tenant);
        professional.setSpecialty(request.specialty());
        professional.setDocumentType(request.documentType());
        professional.setDocumentNumber(request.documentNumber());
        professional.setDocumentState(request.documentState());
        professional.setBio(request.bio());
        professional.setActive(true);

        professional = professionalRepository.save(professional);

        // Converter para Response
        return new ProfessionalResponse(
                professional.getId(),
                professional.getUser().getId(),
                professional.getTenant().getId(),
                professional.getSpecialty(),
                professional.getDocumentType(),
                professional.getDocumentNumber(),
                professional.getDocumentState(),
                professional.getBio(),
                professional.isActive(),
                professional.getCreatedAt(),
                professional.getUpdatedAt()
        );
    }
}

