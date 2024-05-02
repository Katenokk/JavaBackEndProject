package com.pethealth.finalproject.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@PrimaryKeyJoinColumn(name = "vomit_id")
public class Fever extends Event {
    private double degrees;

    public Fever(LocalDate date, String comment, double degrees) {
        super(date, comment);
        this.degrees = degrees;
    }
}
