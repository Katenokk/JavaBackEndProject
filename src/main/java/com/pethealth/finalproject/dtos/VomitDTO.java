package com.pethealth.finalproject.dtos;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonTypeName("vomit")
public class VomitDTO extends EventDTO {
    private boolean hasFood;

    private boolean hasHairball;
}
