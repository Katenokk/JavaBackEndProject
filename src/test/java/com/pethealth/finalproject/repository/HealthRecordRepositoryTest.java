package com.pethealth.finalproject.repository;

import com.pethealth.finalproject.model.*;
import com.pethealth.finalproject.security.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
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
class HealthRecordRepositoryTest {

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
        LocalDate dateOfBirth = LocalDate.of(200, 1, 1);
        Date dateOfBirthOld = Date.from(dateOfBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        catto = new Cat("Catto", dateOfBirthOld, false, List.of(CatDiseases.IBD), CatBreeds.BENGAL, owner, null);
        petRepository.save(catto);

        healthRecord1 = new HealthRecord(catto);
        healthRecordRepository.save(healthRecord1);


        LocalDate now = LocalDate.now();
        Date dateNow = Date.from(now.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        weight1 = new Weight( dateNow, 10.5, healthRecord1);
        weight2= new Weight( dateNow, 11.5, healthRecord1);
    }

    @AfterEach
    void tearDown() {
        healthRecordRepository.deleteAll();
        weightRepository.deleteAll();
    }

    @Test
    void findByIdAndInitializeWeights() {
        healthRecord1.getWeights().addAll(List.of(weight1, weight2));
        weightRepository.saveAll(List.of(weight1, weight2));

        Optional<HealthRecord> foundRecord = healthRecordRepository.findByIdAndInitializeWeights(healthRecord1.getId());

        assertTrue(foundRecord.isPresent());
        HealthRecord foundHealthRecord = foundRecord.get();
        assertNotNull(foundHealthRecord.getWeights());
        assertEquals(2, foundHealthRecord.getWeights().size());
        assertTrue(foundHealthRecord.getWeights().contains(weight1));
        assertTrue(foundHealthRecord.getWeights().contains(weight2));
    }
}