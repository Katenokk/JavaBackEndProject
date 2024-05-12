package com.pethealth.finalproject.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@PrimaryKeyJoinColumn(name = "medication_id")
public class Medication extends Event {

    @NotNull(message = "Name is required")
    @Pattern(regexp = "^[\\p{L}\\s]{3,}$", message = "Name must be at least 3 characters long and contain only letters")
    private String name;

    @NotNull
    @Positive
    private Double dosageInMgPerDay;

    public Medication(Date date, String comment, String name, Double dosageInMgPerDay) {
        super(date, comment);
        this.name = name;
        this.dosageInMgPerDay = dosageInMgPerDay;
    }
}
