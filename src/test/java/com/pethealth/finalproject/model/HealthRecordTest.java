package com.pethealth.finalproject.model;

import com.pethealth.finalproject.repository.HealthRecordRepository;
import com.pethealth.finalproject.repository.PetRepository;
import com.pethealth.finalproject.repository.WeightRepository;
import com.pethealth.finalproject.security.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HealthRecordTest {

    @Autowired
    private HealthRecordRepository healthRecordRepository;

    @Autowired
    private WeightRepository weightRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetRepository petRepository;

    private Weight weight1;
    private Weight weight2;

    private HealthRecord healthRecord1;

    private Cat catto;
    private Owner owner;

    @BeforeEach
    void setUp() {
        owner = new Owner("New Owner", "new-owner", "1234", new ArrayList<>(), "owner@mail.com");
        userRepository.save(owner);
        catto = new Cat("Catto", LocalDate.of(200, 1, 1), false, List.of(CatDiseases.IBD), CatBreeds.BENGAL, owner, null);
        petRepository.save(catto);

        healthRecord1 = new HealthRecord(catto);
        healthRecordRepository.save(healthRecord1);

        weight1 = new Weight( LocalDate.now(), 10.5, healthRecord1);
        weight2= new Weight( LocalDate.now(), 11.5, healthRecord1);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testCreateHealthRecord(){
        healthRecord1.getWeights().addAll(List.of(weight1, weight2));
        weightRepository.saveAll(List.of(weight1, weight2));
        assertNotNull(healthRecord1.getId());
    }

    @Test
    @Transactional
    void testPetHealthRecordAssociation() {
        HealthRecord healthRecord = new HealthRecord();

        Cat testCat = new Cat("Catto", LocalDate.of(2000, 1, 1), false, List.of(CatDiseases.IBD), CatBreeds.BENGAL, owner, null);

        healthRecord.setPet(testCat);
        testCat.setHealthRecord(healthRecord);
        petRepository.save(testCat);
        healthRecordRepository.save(healthRecord);

        HealthRecord foundHealthRecord = healthRecordRepository.findById(healthRecord.getId()).orElse(null);

        assertNotNull(foundHealthRecord);
        assertNotNull(foundHealthRecord.getPet());
        assertEquals(testCat, foundHealthRecord.getPet());
    }

    @Test
    void testAddWeight() {
        catto = petRepository.save(catto);
//        petRepository.flush(); // no hacen falta los flush

        catto = (Cat) petRepository.findById(catto.getId()).orElse(null);
//importante crear el HealthRecord con un pet asignado
        HealthRecord healthRecord = new HealthRecord(catto);
        catto.setHealthRecord(healthRecord);

        catto = petRepository.save(catto);
        healthRecordRepository.save(healthRecord);

//importante asignar el healthrecord a weight cuando se crea
        Weight weight = new Weight(LocalDate.now(), 10.5, healthRecord);
        weight = weightRepository.save(weight);
        healthRecord.addWeight(weight);

//        healthRecordRepository.flush();

        HealthRecord foundHealthRecord = healthRecordRepository.findById(healthRecord.getId()).orElse(null);

        assertNotNull(foundHealthRecord);
        assertTrue(healthRecord.getWeights().contains(weight));
        assertTrue(foundHealthRecord.getWeights().contains(weight));
        assertEquals(healthRecord, weight.getHealthRecord());
    }


}