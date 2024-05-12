package com.pethealth.finalproject.model;

import com.pethealth.finalproject.repository.EventRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class MedicationTest {

    @Autowired
    private EventRepository eventRepository;

    @Test
    void MedicationCreationTest() {
        Medication medication = new Medication(new Date(), "test comment", "test med", 1.0);
        assertNotNull(medication);
    }

    @ParameterizedTest
    @DisplayName("Should validate correct names")
    @ValueSource(strings = {"abc", "Tilosina", "Cerenia"})
    void testNameValidation_Valid(String validName) {
        Medication testMed = new Medication(new Date(), "comment", validName, 1.0);
        assertDoesNotThrow(() -> eventRepository.save(testMed));
    }

    @ParameterizedTest
    @DisplayName("Should validate incorrect names")
    @ValueSource(strings = {"", "12", "a", "ab", "abc1", "abc!", "abc*"})
    void testNameValidation_Invalid(String invalidName) {
        Medication testMed2 = new Medication(new Date(), "comment", invalidName, 1.0);
        assertThrows(ConstraintViolationException.class, () -> eventRepository.save(testMed2));
    }

    @ParameterizedTest
    @ValueSource(doubles = {36.5, 0, -1})
    void testDosage(Double dosage) {
        Medication testMed3 = new Medication(new Date(), "comment", "name", dosage);

        if (dosage > 0) {
            assertEquals(dosage, testMed3.getDosageInMgPerDay());
        } else {
            assertThrows(ConstraintViolationException.class, () -> eventRepository.save(testMed3));
        }
    }

}