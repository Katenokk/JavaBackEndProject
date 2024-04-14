package com.pethealth.finalproject.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "dog")
@PrimaryKeyJoinColumn(name="id")
public class Dog extends Pet{

    public Dog(Long id, String name, String dateOfBirth, boolean isSpayedOrNeutered, List<DogDiseases> chronicDiseases, DogBreeds dogBreed) {
        super(id, name, dateOfBirth, isSpayedOrNeutered);
        this.chronicDiseases = chronicDiseases;
        this.dogBreed = dogBreed;
    }

    @Enumerated(EnumType.STRING)
    private List<DogDiseases> chronicDiseases;

    @Enumerated(EnumType.STRING)
    private DogBreeds dogBreed;
}
