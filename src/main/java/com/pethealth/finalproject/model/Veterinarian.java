package com.pethealth.finalproject.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.pethealth.finalproject.security.models.Role;
import com.pethealth.finalproject.security.models.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@JsonIdentityInfo( generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Entity
@Data
@NoArgsConstructor
@DiscriminatorValue("VETERINARIAN")
public class Veterinarian extends User {

    @EqualsAndHashCode.Include
    @Email(message = "Email should be valid")
    @Pattern(regexp = ".+@.+\\..+", message = "Email should have a valid format")
    private String email;

    @OneToMany(mappedBy = "veterinarian")
    private Set<Pet> treatedPets;

    public Veterinarian(String name, String username, String password, Collection<Role> roles, String email) {
        super(name, username, password, roles);
        this.email = email;
    }

    public void addPet(Pet pet) {
        if (treatedPets == null) {
            treatedPets = new HashSet<>();
        }
        treatedPets.add(pet);
        pet.setVeterinarian(this);
    }

    public void removePet(Pet pet) {
        treatedPets.remove(pet);
        pet.setVeterinarian(null); // Clear the association
    }

}
