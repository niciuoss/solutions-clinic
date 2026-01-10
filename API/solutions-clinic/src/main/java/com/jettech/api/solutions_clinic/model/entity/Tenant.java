package com.jettech.api.solutions_clinic.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity(name = "tenant")
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String cnpj;

    @Enumerated(EnumType.STRING)
    private PlanType planType;
    private String address;
    private String phone;
    private boolean active;

    @Column(length = 64, unique = true)
    private String subdomain;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeTenant type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ColumnDefault("'PENDING_SETUP'")
    private TenantStatus status = TenantStatus.PENDING_SETUP;

    private LocalDate trialEndsAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
