package com.pethealth.finalproject.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "discriminator")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CatDTO.class, name = "cat"),
        @JsonSubTypes.Type(value = DogDTO.class, name = "dog")
})
public class PetDTO {

    private Long id;

    private String name;

    private LocalDate dateOfBirth;

    private boolean isSpayedOrNeutered;

    private Owner owner;

    private Veterinarian veterinarian;



}
