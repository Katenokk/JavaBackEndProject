package com.pethealth.finalproject.repository;

import com.pethealth.finalproject.model.*;
import com.pethealth.finalproject.security.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
//@SpringBootTest
@SpringBootTest
class WeightRepositoryTest {
//        @Autowired
//        private TestEntityManager entityManager;

        @Autowired
        private WeightRepository weightRepository;

        @Autowired
        private HealthRecordRepository healthRecordRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private PetRepository petRepository;

        @Test
        public void findAllBetweenDates_ReturnsCorrectWeights() {
            Owner owner = new Owner("New Owner", "new-owner", "1234", new ArrayList<>(), "owner@mail.com");
            userRepository.save(owner);
            Cat catto = new Cat("Catto", LocalDate.of(200, 1, 1), false, List.of(CatDiseases.IBD), CatBreeds.BENGAL, owner, null);
            petRepository.save(catto);

            HealthRecord healthRecord1 = new HealthRecord(catto);
            healthRecordRepository.save(healthRecord1);

            Weight weight1 = new Weight( LocalDate.of(2022, 1, 1), 10.5, healthRecord1);
            Weight weight2= new Weight( LocalDate.of(2022, 1, 2), 11.5, healthRecord1);
            Weight weight3= new Weight( LocalDate.of(2022, 1, 3), 12.5, healthRecord1);
            weightRepository.save(weight1);
            weightRepository.save(weight2);
            weightRepository.save(weight3);

            List<Weight> weights = weightRepository.findAllBetweenDates(LocalDate.of(2022, 1, 1), LocalDate.of(2022, 1, 2));

            assertEquals(2, weights.size());
            assertEquals(weight1.getDay(), weights.get(0).getDay());
            assertEquals(weight2.getDay(), weights.get(1).getDay());
        }


}