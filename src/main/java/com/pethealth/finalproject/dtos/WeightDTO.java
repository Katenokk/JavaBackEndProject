package com.pethealth.finalproject.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class WeightDTO {
    private Long id;
    private LocalDate day;
    private double weight;
}
