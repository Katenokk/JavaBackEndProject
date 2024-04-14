package com.pethealth.finalproject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "dog")
@PrimaryKeyJoinColumn(name="id")
public class Dog extends Pet{

    public Dog(String name, LocalDate dateOfBirth, boolean isSpayedOrNeutered, List<DogDiseases> chronicDiseases, DogBreeds dogBreed) {
        super(name, dateOfBirth, isSpayedOrNeutered);
        this.chronicDiseases = chronicDiseases;
        this.dogBreed = dogBreed;
    }
    @NotNull(message="chronic disease is required")
    @NotEmpty(message = "At least one chronic disease is required")
    @Enumerated(EnumType.STRING)
    private List<DogDiseases> chronicDiseases;

    @NotNull(message="breed is required")
    @Enumerated(EnumType.STRING)
    private DogBreeds dogBreed;
}
