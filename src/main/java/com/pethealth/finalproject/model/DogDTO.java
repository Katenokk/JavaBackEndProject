package com.pethealth.finalproject.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@JsonTypeName("cat")
public class DogDTO extends PetDTO{
    @NotNull(message="chronic disease is required")
    @NotEmpty(message = "At least one chronic disease is required")
    private List<DogDiseases> chronicDiseases;

    private DogBreeds dogBreed;
//    private Owner owner;
//
//    private Veterinarian veterinarian;
}
