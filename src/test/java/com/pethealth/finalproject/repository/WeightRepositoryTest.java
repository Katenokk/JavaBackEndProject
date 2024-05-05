package com.pethealth.finalproject.repository;

import com.pethealth.finalproject.model.*;
import com.pethealth.finalproject.security.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
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
            LocalDate dateOfBirth = LocalDate.of(200, 1, 1);
            Date dateOfBirthOld = Date.from(dateOfBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
            Cat catto = new Cat("Catto", dateOfBirthOld, false, List.of(CatDiseases.IBD), CatBreeds.BENGAL, owner, null);
            petRepository.save(catto);

            HealthRecord healthRecord1 = new HealthRecord(catto);
            healthRecordRepository.save(healthRecord1);

            LocalDate localDate1 = LocalDate.of(2022, 1, 2);
            Date date1 = Date.from(localDate1.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
            LocalDate localDate2 = LocalDate.of(2022, 1, 3);
            Date date2 = Date.from(localDate2.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
            LocalDate localDate3 = LocalDate.of(2022, 1, 4);
            Date date3 = Date.from(localDate3.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
            Weight weight1 = new Weight(date1, 10.5, healthRecord1);
            Weight weight2 = new Weight(date2, 11.5, healthRecord1);
            Weight weight3 = new Weight(date3, 12.5, healthRecord1);
            weightRepository.save(weight1);
            weightRepository.save(weight2);
            weightRepository.save(weight3);

            LocalDate startLocalDate = LocalDate.of(2022, 1, 1);
            Date startDate = Date.from(startLocalDate.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
            LocalDate endLocalDate = LocalDate.of(2022, 1, 3);
            Date endDate = Date.from(endLocalDate.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
            List<Weight> weights = weightRepository.findAllBetweenDates(startDate, endDate);

            assertEquals(2, weights.size());
            assertEquals(weight1.getDay(), weights.get(0).getDay());
            assertEquals(weight2.getDay(), weights.get(1).getDay());
        }


}