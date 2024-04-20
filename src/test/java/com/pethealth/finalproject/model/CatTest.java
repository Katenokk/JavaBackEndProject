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
        newOwner = new Owner("New Owner", "new_owner", "1234", new ArrayList<>(), "owner@mail.com");
        userRepository.save(newOwner);
        newVet = new Veterinarian("New Vet", "new_vet", "0000",  new ArrayList<>(), "vet@mail.com");
        userRepository.save(newVet);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        petRepository.deleteAll();
    }

    @Test
    void catCreationTest(){
        assertNotNull(newCat);
        assertEquals("Níobe", newCat.getName());
    }

    @Test
    void assignOwner(){
        newCat.setOwner(newOwner);
        assertEquals("New Owner", newCat.getOwner().getName());
        assertEquals(newOwner, newCat.getOwner());
        newOwner.addPet(newCat);
        petRepository.save(newCat);
        assertFalse(newOwner.getOwnedPets().isEmpty());
        assertTrue(newOwner.getOwnedPets().contains(newCat));
        System.out.println("New Cat ID: " + newCat.getId());
        System.out.println("New Owner ID: " + newOwner.getId());
        System.out.println("Owner's Pets: " + newOwner.getOwnedPets());
    }

    @Test
    void assignVeterinarian(){
        newCat.setVeterinarian(newVet);
        newCat.setOwner(newOwner);
        newVet.addPet(newCat);
        petRepository.save(newCat);
        assertEquals("New Vet", newCat.getVeterinarian().getName());
        assertEquals(newVet, newCat.getVeterinarian());
        assertFalse(newVet.getTreatedPets().isEmpty());
        System.out.println("Vet's Pets: " + newVet.getTreatedPets());
        assertTrue(newVet.getTreatedPets().contains(newCat));
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