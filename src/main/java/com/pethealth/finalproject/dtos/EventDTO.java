package com.pethealth.finalproject.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "discriminator")
@JsonSubTypes({
        @JsonSubTypes.Type(value = VomitDTO.class, name = "vomit"),
        @JsonSubTypes.Type(value = FeverDTO.class, name = "fever"),
        @JsonSubTypes.Type(value = MedicationDTO.class, name = "medication")
})
public class EventDTO {
    private Long id;


    @PastOrPresent(message = "Date must be in the past or present")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private Date date;

    @Size(max = 255)
    private String comment;
}
