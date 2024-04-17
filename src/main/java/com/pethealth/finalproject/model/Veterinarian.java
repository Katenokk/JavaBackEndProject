package com.pethealth.finalproject.model;

import com.pethealth.finalproject.security.models.Role;
import com.pethealth.finalproject.security.models.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
//@AllArgsConstructor
//@Table(name="veterinarian")
@DiscriminatorValue("VETERINARIAN")
public class Veterinarian extends User {

    private String email;

    @OneToMany(mappedBy = "veterinarian")
    private Set<Pet> pets;

    public Veterinarian(String name, String username, String password, Collection<Role> roles, String email) {
        super(name, username, password, roles);
        this.email = email;
        this.pets = new HashSet<>();
    }
}
