package com.pethealth.finalproject.security.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminDTO {
    private String name;

    private String username;

    private String password;
}
