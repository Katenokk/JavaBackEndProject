package com.pethealth.finalproject.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class HealthRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "healthRecord")
    private Pet pet;

    @OneToMany(mappedBy = "healthRecord", cascade = CascadeType.ALL)
    private List<Weight> weights;

    @PostLoad
    private void initWeights(){
        Hibernate.initialize(this.weights);
    }
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
}
