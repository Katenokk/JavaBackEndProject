package com.pethealth.finalproject.dtos;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.pethealth.finalproject.model.CatBreeds;
import com.pethealth.finalproject.model.CatDiseases;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonTypeName("cat")
public class CatReadDTO extends PetReadDTO{
    private List<CatDiseases> chronicDiseases;
    private CatBreeds catBreed;
}
