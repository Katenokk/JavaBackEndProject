package com.pethealth.finalproject.security.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserUpdateDTO {
    @Pattern(regexp = "^[\\p{L}\\s]{3,}$", message = "Name must be at least 3 characters long and contain only letters")
    private String name;

//    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters long")
//    @Pattern(regexp = "^[a-zA-Z0-9_. -]*$", message = "Username can only contain letters, numbers, spaces, underscores, hyphens, and periods")
//    private String username;

    @Size(min = 4, message = "Password must be at least 4 characters long")
    private String password;

    @Email(message = "Email should be valid")
    private String email;
}
