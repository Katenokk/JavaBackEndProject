package com.pethealth.finalproject.dtos;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonTypeName("fever")
public class FeverDTO extends EventDTO{
    @NotNull
    @Positive
    private Double degrees;
}
