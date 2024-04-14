package com.pethealth.finalproject.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "cat")
@PrimaryKeyJoinColumn(name="id")
public class Cat extends Pet {
    public Cat(String name, LocalDate dateOfBirth, boolean isSpayedOrNeutered, List<CatDiseases> chronicDiseases, CatBreeds catBreed) {
        super(name, dateOfBirth, isSpayedOrNeutered);
        this.chronicDiseases = chronicDiseases;
        this.catBreed = catBreed;
    }

    @Enumerated(EnumType.STRING)
    private List<CatDiseases> chronicDiseases;

    @Enumerated(EnumType.STRING)
    private CatBreeds catBreed;
}
