package com.jettech.api.solutions_clinic.model.usecase.financial;

import com.jettech.api.solutions_clinic.model.entity.FinancialCategory;
import com.jettech.api.solutions_clinic.model.entity.Tenant;
import com.jettech.api.solutions_clinic.model.repository.FinancialCategoryRepository;
import com.jettech.api.solutions_clinic.model.repository.TenantRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jettech.api.solutions_clinic.exception.ApiError;
import com.jettech.api.solutions_clinic.exception.AuthenticationFailedException;
import com.jettech.api.solutions_clinic.exception.DuplicateEntityException;
import com.jettech.api.solutions_clinic.exception.EntityNotFoundException;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultCreateFinancialCategoryUseCase implements CreateFinancialCategoryUseCase {

    private final FinancialCategoryRepository financialCategoryRepository;
    private final TenantRepository tenantRepository;

    @Override
    @Transactional
    public FinancialCategoryResponse execute(CreateFinancialCategoryRequest request) throws AuthenticationFailedException {
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Clínica", request.tenantId()));

        // Verificar se já existe categoria com o mesmo nome
        if (financialCategoryRepository.existsByNameAndTenantId(request.name(), request.tenantId())) {
            throw new DuplicateEntityException(ApiError.DUPLICATE_CATEGORY_NAME);
        }

        FinancialCategory category = new FinancialCategory();
        category.setTenant(tenant);
        category.setName(request.name());
        category.setType(request.type());
        category.setActive(true);

        category = financialCategoryRepository.save(category);

        return new FinancialCategoryResponse(
                category.getId(),
                category.getTenant().getId(),
                category.getName(),
                category.getType(),
                category.isActive(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
