package com.pethealth.finalproject.dtos;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.pethealth.finalproject.model.DogBreeds;
import com.pethealth.finalproject.model.DogDiseases;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonTypeName("cat")
public class DogReadDTO extends PetReadDTO {
    private List<DogDiseases> chronicDiseases;
    private DogBreeds dogBreed;
}
