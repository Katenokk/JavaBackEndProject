package com.pethealth.finalproject.dtos;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.pethealth.finalproject.model.CatDTO;
import com.pethealth.finalproject.model.DogDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "discriminator")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CatDTO.class, name = "cat"),
        @JsonSubTypes.Type(value = DogDTO.class, name = "dog")
})
public class PetReadDTO {
    private String name;
    private LocalDate dateOfBirth;
    private boolean isSpayedOrNeutered;
    private Long ownerId;
    private String ownerName;
    private Long veterinarianId;
    private String veterinarianName;

}