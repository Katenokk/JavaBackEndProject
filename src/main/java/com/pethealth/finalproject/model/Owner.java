package com.pethealth.finalproject.model;

import com.pethealth.finalproject.security.models.Role;
import com.pethealth.finalproject.security.models.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data//mirar el hashcode y equals!!
@NoArgsConstructor
//@AllArgsConstructor
@DiscriminatorValue("OWNER")
public class Owner extends User {

    private String email;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Pet> pets;

    public Owner(String name, String username, String password, Collection<Role> roles, String email) {
        super(name, username, password, roles);
        this.email = email;
        this.pets = new HashSet<>();//prueba para ver si tostring no falla en el test
    }
    //prueba
    public void addPet(Pet pet) {
        // Check if pets set is null and initialize if necessary
        if (pets == null) {
            pets = new HashSet<>();
        }
        // Add the pet to the set
        pets.add(pet);
        // Set the owner of the pet
        pet.setOwner(this);
    }

    @Override
    public String toString() {
        String firstPetName = !getPets().isEmpty() ? getPets().iterator().next().getName() : "";
        return "Owner{" +
                "name='" + getName() + '\'' +
                "user name='" + getUsername() + '\'' +
                ", email='" + email + '\'' +
                ", firstPet='" + firstPetName + "'" +
                // Add other attributes as needed
                '}';
    }



}
