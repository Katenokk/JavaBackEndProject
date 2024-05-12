package com.pethealth.finalproject.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "discriminator")
@JsonSubTypes({
        @JsonSubTypes.Type(value = VomitDTO.class, name = "vomit"),
        @JsonSubTypes.Type(value = FeverDTO.class, name = "fever")
})
public class EventDTO {
    private Long id;
    @NotNull
    @PastOrPresent(message = "Date must be in the past or present")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private Date date;
    private String comment;
//    private String discriminator;
}
