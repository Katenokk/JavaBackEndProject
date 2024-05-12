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
    @JsonBackReference
    private Pet pet;

//    @OneToMany(mappedBy = "healthRecord")
    @OneToMany(mappedBy = "healthRecord", cascade = CascadeType.ALL)
    private List<Weight> weights;

// implementar luego:
//    @OneToMany(mappedBy = "petHealthRecord", cascade = CascadeType.ALL)
//    private List<Medication> medications;
//
    @OneToMany(mappedBy = "petHealthRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> events;


    public HealthRecord(Pet pet) {
        this.pet = pet;
        this.weights = new ArrayList<>();
        this.events = new ArrayList<>();
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

    public void addEvent(Event event) {
        if(events == null) {
            events = new ArrayList<>();
        } else {
            events.add(event);
            event.setPetHealthRecord(this);
        }
    }

    public void removeEvent(Event event) {
        events.remove(event);
    }
}
