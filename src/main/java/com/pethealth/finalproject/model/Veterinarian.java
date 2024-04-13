package com.pethealth.finalproject.model;

import com.pethealth.finalproject.security.models.Role;
import com.pethealth.finalproject.security.models.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
//@Table(name="veterinarian")
@DiscriminatorValue("VETERINARIAN")
public class Veterinarian extends User {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
    private String email;

    public Veterinarian(Long id, String name, String username, String password, Collection<Role> roles, String email) {
        super(id, name, username, password, roles);
        this.email = email;
    }
}
