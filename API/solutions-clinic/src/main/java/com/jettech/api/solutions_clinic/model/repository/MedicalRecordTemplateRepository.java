package com.jettech.api.solutions_clinic.model.repository;

import com.jettech.api.solutions_clinic.model.entity.MedicalRecordTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MedicalRecordTemplateRepository extends JpaRepository<MedicalRecordTemplate, UUID> {

    List<MedicalRecordTemplate> findByTenantId(UUID tenantId);

    List<MedicalRecordTemplate> findByTenantIdAndActive(UUID tenantId, boolean active);

    Optional<MedicalRecordTemplate> findByIdAndTenantId(UUID id, UUID tenantId);

    List<MedicalRecordTemplate> findByTenantIdAndProfessionalTypeAndActive(
            UUID tenantId, String professionalType, boolean active);

    /**
     * Templates disponíveis para o tenant: globais (tenant_id IS NULL) + específicos da clínica.
     * Usado para listar o que a clínica pode usar ao criar prontuários.
     */
    @Query("""
        SELECT t FROM MedicalRecordTemplate t
        WHERE t.active = true
          AND (t.tenant IS NULL OR t.tenant.id = :tenantId)
        ORDER BY t.tenant.id NULLS FIRST, t.name
        """)
    List<MedicalRecordTemplate> findAvailableForTenant(@Param("tenantId") UUID tenantId);

    /**
     * Mesmo que findAvailableForTenant, filtrado por professional_type.
     * Inclui também templates genéricos (professional_type IS NULL).
     */
    @Query("""
        SELECT t FROM MedicalRecordTemplate t
        WHERE t.active = true
          AND (t.tenant IS NULL OR t.tenant.id = :tenantId)
          AND (t.professionalType IS NULL OR t.professionalType = :professionalType)
        ORDER BY t.tenant.id NULLS FIRST, t.name
        """)
    List<MedicalRecordTemplate> findAvailableForTenantAndProfessionalType(
            @Param("tenantId") UUID tenantId,
            @Param("professionalType") String professionalType);

    /**
     * Busca por ID visível ao tenant: template global OU do tenant.
     */
    @Query("""
        SELECT t FROM MedicalRecordTemplate t
        WHERE t.id = :id
          AND (t.tenant IS NULL OR t.tenant.id = :tenantId)
        """)
    Optional<MedicalRecordTemplate> findByIdAvailableForTenant(
            @Param("id") UUID id,
            @Param("tenantId") UUID tenantId);
}
