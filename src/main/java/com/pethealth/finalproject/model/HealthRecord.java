package com.pethealth.finalproject.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@DynamicUpdate
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class HealthRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "healthRecord")
//    @JsonIdentityInfo(
//            generator = ObjectIdGenerators.PropertyGenerator.class,
//            property = "id")
//    @JsonIdentityReference(alwaysAsId = true)
//    @JsonTypeInfo(
//            use = JsonTypeInfo.Id.NAME,
//            include = JsonTypeInfo.As.PROPERTY,
//            property = "type")
//    @JsonSubTypes({
//            @JsonSubTypes.Type(value = Cat.class, name = "cat"),
//            @JsonSubTypes.Type(value = Dog.class, name = "dog")
//    })
    private Pet pet;

    @OneToMany(mappedBy = "healthRecord", cascade = CascadeType.ALL)
    private List<Weight> weights;

//    @PostLoad
//    private void initWeights(){
//        Hibernate.initialize(this.weights);
//    }
// implementar luego:
//    @OneToMany(mappedBy = "petHealthRecord", cascade = CascadeType.ALL)
//    private List<Medication> medications;
//
//    @OneToMany(mappedBy = "petHealthRecord", cascade = CascadeType.ALL)
//    private List<Event> events;


    public HealthRecord(Pet pet) {
        this.pet = pet;
        this.weights = new ArrayList<>();
    }

    public void addWeight(Weight weight) {
        if(weights == null) {
            weights = new ArrayList<>();
        } else {
            weights.add(weight);
            weight.setHealthRecord(this);
        }
    }

    public void removeWeight(Weight weight) {
        weights.remove(weight);
        weight.setHealthRecord(null);
    }
}
