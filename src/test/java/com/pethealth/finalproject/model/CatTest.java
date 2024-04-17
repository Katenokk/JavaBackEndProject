package com.pethealth.finalproject.model;

//import jakarta.validation.ConstraintViolation;
import com.pethealth.finalproject.repository.PetRepository;
import com.pethealth.finalproject.security.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CatTest {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;
    private Cat newCat;
    private Owner newOwner;
    private Veterinarian newVet;
//no funciona la validacion con jackarta, hacer test en el controller
//    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//    private final Validator validator = factory.getValidator();
    @BeforeEach
    void setUp() {
        LocalDate dateOfBirth = LocalDate.of(2010,06,01);
        newCat = new Cat("Níobe", dateOfBirth, true, List.of(CatDiseases.IBD), CatBreeds.MIXED, null, null);
        newOwner = new Owner("New Owner", "new_owner", "1234", new HashSet<>(), "owner@mail.com");
        newVet = new Veterinarian("New Vet", "new_vet", "0000",  new HashSet<>(), "vet@mail.com");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void catCreationTest(){
        assertNotNull(newCat);
        assertEquals("Níobe", newCat.getName());
    }

    @Test
    void assignOwner(){
        petRepository.save(newCat);
        userRepository.save(newOwner);
        newCat.setOwner(newOwner);
        assertEquals("New Owner", newCat.getOwner().getName());
        assertEquals(newOwner, newCat.getOwner());
        newOwner.addPet(newCat);
        assertNotNull(newOwner.getPets());
//        assertTrue(newOwner.getPets().contains(newCat));
        System.out.println("New Cat ID: " + newCat.getId());
        System.out.println("New Owner ID: " + newOwner.getId());
//        System.out.println("Owner's Pets: " + newOwner.getPets());
    }

    @Test
    void assignVeterinarian(){
        newCat.setVeterinarian(newVet);
        assertEquals("New Vet", newCat.getVeterinarian().getName());
        assertEquals(newVet, newCat.getVeterinarian());
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