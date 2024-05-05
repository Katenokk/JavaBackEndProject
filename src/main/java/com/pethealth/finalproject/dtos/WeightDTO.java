package com.pethealth.finalproject.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
public class WeightDTO {
    private Long id;
    private Date day;
    private double weight;
}
