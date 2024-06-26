package com.pethealth.finalproject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Table(name = "cat")
@PrimaryKeyJoinColumn(name="id")
public class Cat extends Pet {
    public Cat(String name, Date dateOfBirth, boolean isSpayedOrNeutered, List<CatDiseases> chronicDiseases, CatBreeds catBreed, Owner owner, Veterinarian veterinarian) {
        super(name, dateOfBirth, isSpayedOrNeutered, owner, veterinarian);
        this.chronicDiseases = chronicDiseases;
        this.catBreed = catBreed;
    }
    @ElementCollection(targetClass = CatDiseases.class)
    @Enumerated(EnumType.STRING)
    @NotNull(message="chronic disease is required")
    @NotEmpty(message = "At least one chronic disease is required")
    private List<CatDiseases> chronicDiseases;

    @Enumerated(EnumType.STRING)
    private CatBreeds catBreed;
}
