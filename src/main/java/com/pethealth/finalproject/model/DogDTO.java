package com.pethealth.finalproject.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@JsonTypeName("cat")
public class DogDTO extends PetDTO{
    private List<DogDiseases> chronicDiseases;

    private DogBreeds dogBreed;
    private Owner owner;

    private Veterinarian veterinarian;
}
