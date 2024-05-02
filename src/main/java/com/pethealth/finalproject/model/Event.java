package com.pethealth.finalproject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @PastOrPresent(message = "Date must be in the past or present")
    private LocalDate date;

    private String comment;

    @ManyToOne
    @JoinColumn(name = "pet_health_record_id")
    private HealthRecord healthRecord;

    public Event(LocalDate date, String comment) {
        this.date = date;
        this.comment = comment;
    }
}
