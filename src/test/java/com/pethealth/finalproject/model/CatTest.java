package com.pethealth.finalproject.model;

//import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class CatTest {
    private Cat newCat;
//no funciona la validacion con jackarta, hacer test en el controller
//    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//    private final Validator validator = factory.getValidator();
    @BeforeEach
    void setUp() {
        LocalDate dateOfBirth = LocalDate.of(2010,06,01);
        newCat = new Cat("Níobe", dateOfBirth, true, List.of(CatDiseases.IBD), CatBreeds.MIXED);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void catCreationTest(){
        assertNotNull(newCat);
        assertEquals("Níobe", newCat.getName());
    }

//    @Test
//    void catInvalidCreation(){
//        Cat cat = new Cat("123", LocalDate.of(2015, 1, 2), false, List.of(CatDiseases.IBD), CatBreeds.MIXED);
//
//        // Validate the Cat instance
//        Set<ConstraintViolation<Cat>> violations = validator.validate(cat);
//
//        // Assert that there are constraint violations
//        assertFalse(violations.isEmpty());
//    }
}