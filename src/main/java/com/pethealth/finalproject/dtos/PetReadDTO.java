package com.pethealth.finalproject.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.pethealth.finalproject.model.CatDTO;
import com.pethealth.finalproject.model.DogDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "discriminator")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CatReadDTO.class, name = "cat"),
        @JsonSubTypes.Type(value = DogReadDTO.class, name = "dog")
})
public class PetReadDTO {
    private String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private Date dateOfBirth;
    private boolean isSpayedOrNeutered;
    private Long ownerId;
    private String ownerName;
    private Long veterinarianId;
    private String veterinarianName;

}