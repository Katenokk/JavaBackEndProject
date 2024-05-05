package com.pethealth.finalproject.model;

import com.pethealth.finalproject.repository.WeightRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import jakarta.validation.ConstraintViolationException;

import java.time.LocalDate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WeightTest {

    @Autowired
    private WeightRepository weightRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void weightCreationTest() {
        Weight weight = new Weight();
        LocalDate date = LocalDate.of(2021, 10, 10);
        Date dateDate = Date.from(date.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        weight.setDay(dateDate);
        weight.setWeight(10.5);
        assertNotNull(weight);
    }

    @Test
    void testDayValidation_FutureDate() {
        Weight weight = new Weight();
        LocalDate date = LocalDate.of(2051, 10, 10);
        Date dateDate = Date.from(date.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        weight.setDay(dateDate); // set a future date
        weight.setWeight(10.0);
        assertThrows(ConstraintViolationException.class, () -> weightRepository.save(weight));
    }

    @Test
    void testWeightValidation_NegativeWeight() {
        Weight weight = new Weight();
        LocalDate date = LocalDate.now();
        Date dateDate = Date.from(date.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        weight.setDay(dateDate);
        weight.setWeight(-10.0); // set a negative weight
        assertThrows(ConstraintViolationException.class, () -> weightRepository.save(weight));
    }


    @ParameterizedTest
    @ValueSource(doubles = {-10.0, 0.0})
    void testWeightValidation_NonPositiveWeight(double weightValue) {
        Weight weight = new Weight();
        LocalDate date = LocalDate.now();
        Date dateDate = Date.from(date.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        weight.setDay(dateDate);
        weight.setWeight(weightValue);
        assertThrows(ConstraintViolationException.class, () -> weightRepository.save(weight));
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L})
    void testDayValidation_FutureDate(long daysToAdd) {
        Weight weight = new Weight();
        LocalDate date = LocalDate.now().plusDays(daysToAdd);
        Date dateDate = Date.from(date.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        weight.setDay(dateDate);
        weight.setWeight(10.0);
        assertThrows(ConstraintViolationException.class, () -> weightRepository.save(weight));
    }


}