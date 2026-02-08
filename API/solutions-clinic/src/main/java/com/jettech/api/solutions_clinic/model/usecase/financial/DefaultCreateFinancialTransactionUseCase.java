package com.jettech.api.solutions_clinic.model.usecase.financial;

import com.jettech.api.solutions_clinic.model.entity.*;
import com.jettech.api.solutions_clinic.model.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jettech.api.solutions_clinic.exception.ApiError;
import com.jettech.api.solutions_clinic.exception.AuthenticationFailedException;
import com.jettech.api.solutions_clinic.exception.EntityNotFoundException;
import com.jettech.api.solutions_clinic.exception.InvalidRequestException;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultCreateFinancialTransactionUseCase implements CreateFinancialTransactionUseCase {

    private final FinancialTransactionRepository financialTransactionRepository;
    private final FinancialCategoryRepository financialCategoryRepository;
    private final TenantRepository tenantRepository;
    private final AppointmentRepository appointmentRepository;
    private final ProfessionalRepository professionalRepository;

    @Override
    @Transactional
    public FinancialTransactionResponse execute(CreateFinancialTransactionRequest request) throws AuthenticationFailedException {
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Clínica", request.tenantId()));

        FinancialTransaction transaction = new FinancialTransaction();
        transaction.setTenant(tenant);
        transaction.setDescription(request.description());
        transaction.setType(request.type());
        transaction.setAmount(request.amount());
        transaction.setDueDate(request.dueDate());
        transaction.setPaymentDate(request.paymentDate());
        transaction.setStatus(request.status());
        transaction.setPaymentMethod(request.paymentMethod());

        // Associar categoria se fornecida
        if (request.categoryId() != null) {
            FinancialCategory category = financialCategoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Categoria", request.categoryId()));
            
            // Validar se o tipo da categoria corresponde ao tipo da transação
            if (category.getType() != request.type()) {
                throw new InvalidRequestException(ApiError.CATEGORY_TYPE_MISMATCH);
            }
            
            transaction.setCategory(category);
        }

        // Associar appointment se fornecido
        if (request.appointmentId() != null) {
            Appointment appointment = appointmentRepository.findById(request.appointmentId())
                    .orElseThrow(() -> new EntityNotFoundException("Agendamento", request.appointmentId()));
            transaction.setAppointment(appointment);
        }

        // Associar professional se fornecido
        if (request.professionalId() != null) {
            Professional professional = professionalRepository.findById(request.professionalId())
                    .orElseThrow(() -> new EntityNotFoundException("Profissional", request.professionalId()));
            transaction.setProfessional(professional);
        }

        transaction = financialTransactionRepository.save(transaction);

        return toResponse(transaction);
    }

    private FinancialTransactionResponse toResponse(FinancialTransaction transaction) {
        return new FinancialTransactionResponse(
                transaction.getId(),
                transaction.getTenant().getId(),
                transaction.getDescription(),
                transaction.getType(),
                transaction.getCategory() != null ? transaction.getCategory().getId() : null,
                transaction.getCategory() != null ? transaction.getCategory().getName() : null,
                transaction.getAmount(),
                transaction.getDueDate(),
                transaction.getPaymentDate(),
                transaction.getStatus(),
                transaction.getPaymentMethod(),
                transaction.getAppointment() != null ? transaction.getAppointment().getId() : null,
                transaction.getProfessional() != null ? transaction.getProfessional().getId() : null,
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );
    }
}
