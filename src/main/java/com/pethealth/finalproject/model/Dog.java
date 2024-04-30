package com.pethealth.finalproject.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Table(name = "dog")
@PrimaryKeyJoinColumn(name="id")
public class Dog extends Pet{

    public Dog(String name, LocalDate dateOfBirth, boolean isSpayedOrNeutered, List<DogDiseases> chronicDiseases, DogBreeds dogBreed, Owner owner, Veterinarian veterinarian) {
        super(name, dateOfBirth, isSpayedOrNeutered, owner, veterinarian);
        this.chronicDiseases = chronicDiseases;
        this.dogBreed = dogBreed;
    }

    @NotNull(message="chronic disease is required")
    @NotEmpty(message = "At least one chronic disease is required")
    @ElementCollection(targetClass = DogDiseases.class)
    @Enumerated(EnumType.STRING)
    private List<DogDiseases> chronicDiseases;

//    @NotNull(message="breed is required")
    @Enumerated(EnumType.STRING)
    private DogBreeds dogBreed;
}
