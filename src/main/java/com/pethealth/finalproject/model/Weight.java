package com.pethealth.finalproject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)//comprobar equals!
@DynamicUpdate
public class Weight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull(message = "Day is required")
    @PastOrPresent(message = "Day must be in the past or present")
    private LocalDate day;

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    private double weight;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pet_health_record_id")
    private HealthRecord healthRecord;

    public Weight(LocalDate day, double weight, HealthRecord healthRecord) {
        this.day = day;
        this.weight = weight;
        this.healthRecord = healthRecord;
    }
}
