package com.pethealth.finalproject.dtos;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonTypeName("medication")
public class MedicationDTO extends EventDTO {


    @Pattern(regexp = "^[\\p{L}\\s]{3,}$", message = "Name must be at least 3 characters long and contain only letters")
    private String name;


    @Positive
    private Double dosageInMgPerDay;
}
