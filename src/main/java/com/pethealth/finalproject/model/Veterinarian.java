package com.pethealth.finalproject.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.pethealth.finalproject.security.models.Role;
import com.pethealth.finalproject.security.models.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@JsonIdentityInfo( generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Entity
@Data
@NoArgsConstructor
//@AllArgsConstructor
//@Table(name="veterinarian")
@DiscriminatorValue("VETERINARIAN")
public class Veterinarian extends User {

    @EqualsAndHashCode.Include
    private String email;

    @OneToMany(mappedBy = "veterinarian")
    private Set<Pet> treatedPets;

    public Veterinarian(String name, String username, String password, Collection<Role> roles, String email) {
        super(name, username, password, roles);
        this.email = email;
        this.treatedPets = new HashSet<>();
    }

    public void addPet(Pet pet) {
        if (treatedPets == null) {
            treatedPets = new HashSet<>();
        }
        treatedPets.add(pet);
//        pet.setVeterinarian(this);
    }

    public void removePet(Pet pet) {
        treatedPets.remove(pet);
        pet.setVeterinarian(null); // Clear the association
    }

    @Override
    public String toString() {
//        String firstPetName = !getTreatedPets().isEmpty() ? getTreatedPets().iterator().next().getName() : "";
        return "Vet{" +
                "name='" + getName() + '\'' +
                "user name='" + getUsername() + '\'' +
                ", email='" + email + '\'' +
//                ", firstPet='" + firstPetName + "'" +
                // terminar resto luego
                '}';
    }
}
