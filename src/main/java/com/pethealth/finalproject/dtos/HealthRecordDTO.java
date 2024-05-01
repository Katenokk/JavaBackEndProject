package com.pethealth.finalproject.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HealthRecordDTO {
    private Long id;
    private List<WeightDTO> weights;
}
