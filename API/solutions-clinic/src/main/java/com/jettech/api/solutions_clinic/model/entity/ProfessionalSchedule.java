package com.jettech.api.solutions_clinic.model.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity(name = "professional_schedules")
@Table(uniqueConstraints = {
    @UniqueConstraint(name = "uk_professional_schedule_day", columnNames = {"professional_id", "day_of_week"})
})
public class ProfessionalSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "professional_id", nullable = false)
    private Professional professional;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek; // MONDAY, TUESDAY, etc.

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime; // Ex: 08:00

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;   // Ex: 18:00

    @Column(name = "lunch_break_start", nullable = false)
    private LocalTime lunchBreakStart; // Ex: 12:00

    @Column(name = "lunch_break_end", nullable = false)
    private LocalTime lunchBreakEnd;   // Ex: 13:00
    
    // Define o tamanho padrão da consulta para gerar os slots (ex: 30 min)
    @Column(name = "slot_duration_minutes", nullable = false)
    private int slotDurationMinutes = 30;

    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
