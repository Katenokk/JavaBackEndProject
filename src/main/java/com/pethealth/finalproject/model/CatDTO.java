package com.pethealth.finalproject.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonTypeName("cat")
public class CatDTO extends PetDTO{

    private List<CatDiseases> chronicDiseases;

    private CatBreeds catBreed;

//    private Owner owner;
//
//    private Veterinarian veterinarian;
}
