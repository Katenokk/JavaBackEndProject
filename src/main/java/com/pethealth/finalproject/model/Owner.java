package com.pethealth.finalproject.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
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
import org.hibernate.annotations.DynamicUpdate;

import java.util.*;


@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@JsonIdentityInfo( generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Entity
@Data
@NoArgsConstructor
@DynamicUpdate
@DiscriminatorValue("OWNER")
public class Owner extends User {
    @EqualsAndHashCode.Include
    @Email(message = "Email should be valid")
    @Pattern(regexp = ".+@.+\\..+", message = "Email should have a valid format")
    private String email;
//quitar eager
    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Pet> ownedPets;
//quitar
    @PostLoad
    private void initPets(){
        Hibernate.initialize(this.ownedPets);
    }

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
//        String firstPetName = !getOwnedPets().isEmpty() ? getOwnedPets().iterator().next().getName() : "";
        return "Owner{" +
                "name='" + getName() + '\'' +
                "user name='" + getUsername() + '\'' +
                ", email='" + email + '\'' +
//                ", firstPet='" + firstPetName + "'" +
                // terminar resto luego
                '}';
    }


}
