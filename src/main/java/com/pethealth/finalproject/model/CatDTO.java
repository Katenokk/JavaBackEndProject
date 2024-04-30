package com.pethealth.finalproject.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonTypeName("cat")
public class CatDTO extends PetDTO{
    @NotNull(message="chronic disease is required")
    @NotEmpty(message = "At least one chronic disease is required")
    private List<CatDiseases> chronicDiseases;

    private CatBreeds catBreed;

}
