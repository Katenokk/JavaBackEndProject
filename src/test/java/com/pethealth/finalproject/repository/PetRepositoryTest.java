package com.pethealth.finalproject.repository;

import com.pethealth.finalproject.model.*;
import com.pethealth.finalproject.security.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class PetRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private HealthRecordRepository healthRecordRepository;

    private Owner petOwner;
    private Cat catto;
    private HealthRecord healthRecord1;

    @Autowired
    private WeightRepository weightRepository;

    @Autowired
    private EventRepository eventRepository;

    @BeforeEach
    void setUp() {
        petOwner = new Owner("Pet Owner", "pet-owner", "1234", new ArrayList<>(), "petowner@mail.com");
        userRepository.save(petOwner);
        LocalDate dateOfBirth = LocalDate.of(200, 1, 1);
        Date dateOfBirthOld = Date.from(dateOfBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        catto = new Cat("Catto", dateOfBirthOld, false, List.of(CatDiseases.IBD), CatBreeds.BENGAL, petOwner, null);
        petRepository.save(catto);

        healthRecord1 = new HealthRecord(catto);
        healthRecordRepository.save(healthRecord1);
    }

    @Test
    void findByIdAndFetchWeightsEagerly() {
        Weight weight1 = new Weight( new Date(), 10.5, healthRecord1);
        Weight weight2= new Weight( new Date(), 11.5, healthRecord1);
        healthRecord1.getWeights().addAll(List.of(weight1, weight2));
        weightRepository.saveAll(List.of(weight1, weight2));

        healthRecordRepository.save(healthRecord1);
        healthRecordRepository.flush();
        catto.setHealthRecord(healthRecord1);
        petRepository.save(catto);

        Optional<Pet> pet = petRepository.findByIdAndFetchWeightsEagerly(catto.getId());
        assertTrue(pet.isPresent());
        assertEquals(2, pet.get().getHealthRecord().getWeights().size());
        assertTrue(pet.get().getHealthRecord().getWeights().contains(weight1));
        assertTrue(pet.get().getHealthRecord().getWeights().contains(weight2));
    }

    @Test
    void findByIdAndFetchEventsEagerly() {
        Event event1 = new Event(new Date(), "Event 1");
        Event event2 = new Event(new Date(), "Event 2");
        healthRecord1.addEvent(event1);
        healthRecord1.addEvent(event2);
        eventRepository.saveAll(List.of(event1, event2));
        healthRecordRepository.save(healthRecord1);
        healthRecordRepository.flush();
        catto.setHealthRecord(healthRecord1);
        petRepository.save(catto);

        Optional<Pet> pet = petRepository.findByIdAndFetchEventsEagerly(catto.getId());
        assertTrue(pet.isPresent());
        assertEquals(2, pet.get().getHealthRecord().getEvents().size());
        assertTrue(pet.get().getHealthRecord().getEvents().contains(event1));
        assertTrue(pet.get().getHealthRecord().getEvents().contains(event2));
    }
}