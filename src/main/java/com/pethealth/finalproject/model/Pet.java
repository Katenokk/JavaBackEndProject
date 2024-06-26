package com.pethealth.finalproject.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
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
    @Pattern(regexp = "^[\\p{L}\\s]{3,}$", message = "Name must be at least 3 characters long and contain only letters")
    private String name;

    @EqualsAndHashCode.Include
    @Past(message = "Date of birth must be in the past")
    @NotNull(message = "Date of birth is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private Date dateOfBirth;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "health_record_id", referencedColumnName = "id")
    private HealthRecord healthRecord;

    public Pet(String name, Date dateOfBirth, boolean isSpayedOrNeutered, Owner owner, Veterinarian veterinarian) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.isSpayedOrNeutered = isSpayedOrNeutered;
        this.owner = owner;
        this.veterinarian = veterinarian;
        this.healthRecord = new HealthRecord(this);
    }

    private boolean isSpayedOrNeutered;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", referencedColumnName  = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Owner owner;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "veterinarian_id", referencedColumnName = "id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Veterinarian veterinarian;


}
