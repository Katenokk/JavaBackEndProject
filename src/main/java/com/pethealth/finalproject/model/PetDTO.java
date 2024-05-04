package com.pethealth.finalproject.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
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

    @NotNull(message = "Name is required")
    @Pattern(regexp = "^[\\p{L}\\s]{3,}$", message = "Name must be at least 3 characters long and contain only letters")
    private String name;

    @Past(message = "Date of birth must be in the past")
    @NotNull(message = "Date of birth is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private Date dateOfBirth;

    @NotNull(message = "You must select is spayed or neutered, yes/no")
    private boolean isSpayedOrNeutered;

    private Owner owner;

    private Veterinarian veterinarian;



}
