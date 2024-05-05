package com.pethealth.finalproject.service;

import com.pethealth.finalproject.dtos.HealthRecordDTO;
import com.pethealth.finalproject.model.*;
import com.pethealth.finalproject.repository.HealthRecordRepository;
import com.pethealth.finalproject.repository.PetRepository;
import com.pethealth.finalproject.repository.WeightRepository;
import com.pethealth.finalproject.security.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HealthRecordServiceTest {

    @Autowired
    private HealthRecordService healthRecordService;
    @Autowired
    private HealthRecordRepository healthRecordRepository;
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private WeightRepository weightRepository;
    @Autowired
    private UserRepository userRepository;


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


        healthRecord1 = new HealthRecord(catto);
        healthRecordRepository.save(healthRecord1);

        LocalDate now = LocalDate.now();
        Date dateNow = Date.from(now.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        weight1 = new Weight( dateNow, 10.5, healthRecord1);
        weight2= new Weight( dateNow, 11.5, healthRecord1);
    }

    @AfterEach
    void tearDown() {
        weightRepository.deleteAll();
    }


//    @Test
//    @Transactional
//    void addWeightToPet() {
//
//        petRepository.save(catto);
//
//        Weight weight = new Weight(LocalDate.now(), 10.5, catto.getHealthRecord());
//
//        healthRecordService.addWeightToPet(catto.getId(), weight);
//
//        Pet foundPet = petRepository.findById(catto.getId()).orElse(null);
//        assertNotNull(foundPet);
//        assertTrue(foundPet.getHealthRecord().getWeights().contains(weight));
//    }

    @Test
    @Transactional
    void addWeightToPet() {

        petRepository.save(catto);

        LocalDate now = LocalDate.now();
        Date dateNow = Date.from(now.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        double weightInKg = 10.5;

        healthRecordService.addWeightToPet(catto.getId(), dateNow, weightInKg);

        Pet foundPet = petRepository.findById(catto.getId()).orElse(null);
        assertNotNull(foundPet);
        //lambda para comprobar si la fecha y peso están en la lista de pesos
        boolean weightAdded = foundPet.getHealthRecord().getWeights().stream()
                .anyMatch(weight -> weight.getDay().equals(dateNow) && weight.getWeight() == weightInKg);
        assertTrue(weightAdded);
    }

    @Test
    @Transactional
    void addWeightToPet_InvalidWeight(){
        petRepository.save(catto);
        LocalDate date = LocalDate.now().plusDays(1);
        Date futureDate = Date.from(date.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        double weightInKg = -10.5;

        assertThrows(IllegalArgumentException.class, () -> healthRecordService.addWeightToPet(catto.getId(), futureDate, weightInKg));
    }

    @Test
    @Transactional
    void getPetHealthRecord_Valid(){
        petRepository.save(catto);
        healthRecordRepository.save(healthRecord1);
        catto.setHealthRecord(healthRecord1);

        healthRecord1.addWeight(weight1);
        healthRecord1.addWeight(weight2);

        weightRepository.save(weight1);
        weightRepository.save(weight2);

        healthRecordRepository.saveAndFlush(healthRecord1);

        HealthRecordDTO healthRecordDTO = healthRecordService.getPetHealthRecord(catto.getId());

        assertNotNull(healthRecordDTO);
        assertEquals(healthRecord1.getId(), healthRecordDTO.getId());
        assertEquals(2, healthRecordDTO.getWeights().size());
    }

    @Test
    @Transactional
    void findWeightsBetWeenDates_Valid(){
        petRepository.save(catto);
        healthRecordRepository.save(healthRecord1);
        catto.setHealthRecord(healthRecord1);

        healthRecord1.addWeight(weight1);
        healthRecord1.addWeight(weight2);

        weightRepository.save(weight1);
        weightRepository.save(weight2);

        healthRecordRepository.saveAndFlush(healthRecord1);

        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(1);
        Date startPastDate = Date.from(startDate.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        Date endFutureDate = Date.from(endDate.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));

        List<Weight> weights = healthRecordService.findWeightsBetweenDates(catto.getId(), startPastDate, endFutureDate);

        assertNotNull(weights);
        assertEquals(2, weights.size());
    }

    @Test
    @Transactional
    void findWeightBetweenDates_EndDateBeforeStartDate(){
        petRepository.save(catto);
        healthRecordRepository.save(healthRecord1);
        catto.setHealthRecord(healthRecord1);
        healthRecord1.addWeight(weight1);
        healthRecord1.addWeight(weight2);
        weightRepository.save(weight1);
        weightRepository.save(weight2);
        healthRecordRepository.saveAndFlush(healthRecord1);

        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now();
        Date startPlusDate = Date.from(startDate.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        Date endNowDate = Date.from(endDate.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));

        assertThrows(IllegalArgumentException.class, () -> healthRecordService.findWeightsBetweenDates(catto.getId(), startPlusDate, endNowDate));
    }

    @Test
    @Transactional
    void findWeightsBetweenDates_NoWeightsFound(){
        petRepository.save(catto);
        healthRecordRepository.save(healthRecord1);
        catto.setHealthRecord(healthRecord1);
        healthRecord1.addWeight(weight1);
        healthRecord1.addWeight(weight2);
        weightRepository.save(weight1);
        weightRepository.save(weight2);
        healthRecordRepository.saveAndFlush(healthRecord1);
        LocalDate startDate = LocalDate.now().plusDays(2);
        LocalDate endDate = LocalDate.now().plusDays(3);
        Date startPlus2 = Date.from(startDate.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        Date endPlus3 = Date.from(endDate.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));

        assertThrows(EntityNotFoundException.class, () -> healthRecordService.findWeightsBetweenDates(catto.getId(), startPlus2, endPlus3));
    }

    @Test
    @Transactional
    void removeWeightFromPet_Valid(){
        petRepository.save(catto);
        healthRecordRepository.save(healthRecord1);
        catto.setHealthRecord(healthRecord1);
        healthRecord1.addWeight(weight1);
        healthRecord1.addWeight(weight2);
        weightRepository.save(weight1);
        weightRepository.save(weight2);
        healthRecordRepository.saveAndFlush(healthRecord1);

        healthRecordService.removeWeightFromPet(weight1.getId(), catto.getId());

        Optional<Pet> foundPet = petRepository.findById(catto.getId());

        assertTrue(foundPet.isPresent());
        assertFalse(foundPet.get().getHealthRecord().getWeights().contains(weight1));
    }

    @Test
    @Transactional
    void removeWeightFromPet_WeightNotFound(){
        petRepository.save(catto);
        healthRecordRepository.save(healthRecord1);
        catto.setHealthRecord(healthRecord1);
        healthRecord1.addWeight(weight1);
        healthRecord1.addWeight(weight2);
        weightRepository.save(weight1);
        weightRepository.save(weight2);
        healthRecordRepository.saveAndFlush(healthRecord1);

        assertThrows(EntityNotFoundException.class, () -> healthRecordService.removeWeightFromPet(100L, catto.getId()));
    }

    @Test
    @Transactional
    void removeWeightFromPet_PetNotFound(){
        petRepository.save(catto);
        weightRepository.save(weight1);
        assertThrows(EntityNotFoundException.class, () -> healthRecordService.removeWeightFromPet(100L, weight1.getId()));
    }

}