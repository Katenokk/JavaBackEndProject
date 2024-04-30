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
        weight.setDay(LocalDate.of(2021, 10, 10));
        weight.setWeight(10.5);
        assertNotNull(weight);
    }

//    @Test
//    void testDayValidation_FutureDate() {
//        Weight weight = new Weight();
//        weight.setDay(LocalDate.now().plusDays(1)); // set a future date
//        weight.setWeight(10.0);
//        assertThrows(ConstraintViolationException.class, () -> weightRepository.save(weight));
//    }
//
//    @Test
//    void testWeightValidation_NegativeWeight() {
//        Weight weight = new Weight();
//        weight.setDay(LocalDate.now());
//        weight.setWeight(-10.0); // set a negative weight
//        assertThrows(ConstraintViolationException.class, () -> weightRepository.save(weight));
//    }


    @ParameterizedTest
    @ValueSource(doubles = {-10.0, 0.0})
    void testWeightValidation_NonPositiveWeight(double weightValue) {
        Weight weight = new Weight();
        weight.setDay(LocalDate.now());
        weight.setWeight(weightValue);
        assertThrows(ConstraintViolationException.class, () -> weightRepository.save(weight));
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L})
    void testDayValidation_FutureDate(long daysToAdd) {
        Weight weight = new Weight();
        weight.setDay(LocalDate.now().plusDays(daysToAdd));
        weight.setWeight(10.0);
        assertThrows(ConstraintViolationException.class, () -> weightRepository.save(weight));
    }


}