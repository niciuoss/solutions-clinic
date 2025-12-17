package com.jettech.api.solutions_clinic.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity(name = "tenant")
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String cnpj;
    @Column(length = 64)
    private String subdomain;

    @Enumerated(EnumType.STRING)
    private TypeTenant type;

}
