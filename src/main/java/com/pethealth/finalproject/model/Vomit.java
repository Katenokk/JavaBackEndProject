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
public class Vomit extends Event {

    private boolean hasFood;

    private boolean hasHairball;
    public Vomit(LocalDate date, String comment, boolean hasFood, boolean hasHairball) {
        super(date, comment);
        this.hasFood = hasFood;
        this.hasHairball = hasHairball;
    }
}
