package com.pethealth.finalproject.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.pethealth.finalproject.security.models.Role;
import com.pethealth.finalproject.security.models.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.*;

//@EqualsAndHashCode(callSuper = false)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@JsonIdentityInfo( generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Entity
@Data
@NoArgsConstructor
//@AllArgsConstructor
@DiscriminatorValue("OWNER")
public class Owner extends User {
    @EqualsAndHashCode.Include
    private String email;


    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Pet> ownedPets;

    public Owner(String name, String username, String password, Collection<Role> roles, String email) {
        super(name, username, password, roles);
        this.email = email;
        this.ownedPets = new HashSet<>();//prueba para ver si tostring no falla en el test
    }

    public void addPet(Pet pet) {
        if (ownedPets == null) {
            ownedPets = new HashSet<>();
        }
        ownedPets.add(pet);
//        pet.setOwner(this);
    }

    public void removePet(Pet pet) {
        ownedPets.remove(pet);
        pet.setOwner(null); // Clear the association
    }

    @Override
    public String toString() {
        String firstPetName = !getOwnedPets().isEmpty() ? getOwnedPets().iterator().next().getName() : "";
        return "Owner{" +
                "name='" + getName() + '\'' +
                "user name='" + getUsername() + '\'' +
                ", email='" + email + '\'' +
                ", firstPet='" + firstPetName + "'" +
                // terminar resto luego
                '}';
    }


}
