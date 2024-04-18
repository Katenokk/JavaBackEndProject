package com.pethealth.finalproject.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonIdentityInfo( generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@DynamicUpdate
@Inheritance(strategy= InheritanceType.JOINED)
@Table(name="pet")
public abstract class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @NotNull(message = "Name is required")
//    @Pattern(regexp = "^[a-zA-Z]{3,}$", message = "Name must be at least 3 characters long and contain only letters")
    @Pattern(regexp = "^[\\p{L}\\s]{3,}$", message = "Name must be at least 3 characters long and contain only letters")
    private String name;

    @EqualsAndHashCode.Include
    @Past(message = "Date of birth must be in the past")
    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    public Pet(String name, LocalDate dateOfBirth, boolean isSpayedOrNeutered, Owner owner, Veterinarian veterinarian) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.isSpayedOrNeutered = isSpayedOrNeutered;
        this.owner = owner;
        this.veterinarian = veterinarian;
    }

    @NotNull(message = "You must select is spayed or neutered, yes/no")
    private boolean isSpayedOrNeutered;

//    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName  = "id")
    private Owner owner;

//    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinarian_id", referencedColumnName = "id")
    private Veterinarian veterinarian;

    @Override
    public String toString() {
        return "Cat{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                '}';
    }

    //prueba
//    public void setOwner(Owner owner) {
//        this.owner = owner;
//        if (owner != null) {
//            owner.getPets().add(this); // Ensure bidirectional relationship is maintained
//        }
//    }
}
