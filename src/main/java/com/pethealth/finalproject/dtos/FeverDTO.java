package com.pethealth.finalproject.dtos;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonTypeName("fever")
public class FeverDTO extends EventDTO{
    private Double degrees;
}
