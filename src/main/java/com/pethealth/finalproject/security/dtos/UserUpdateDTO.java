package com.pethealth.finalproject.security.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserUpdateDTO {
    private String name;
    private String username;
    private String password;
    private String email;
}
