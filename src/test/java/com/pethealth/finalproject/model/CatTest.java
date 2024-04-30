package com.pethealth.finalproject.model;

//import jakarta.validation.ConstraintViolation;
import com.pethealth.finalproject.repository.PetRepository;
import com.pethealth.finalproject.security.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Collections;
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


    @ParameterizedTest
    @DisplayName("Should validate incorrect names")
    @ValueSource(strings = {"", "12", "a", "ab", "abc1", "abc!", "abc*"})
    void testNameValidation_Invalid(String invalidName) {
        Cat cat = new Cat(invalidName, LocalDate.of(2020, 1, 1), true, List.of(CatDiseases.IBD), CatBreeds.MIXED, newOwner, newVet);
        assertThrows(ConstraintViolationException.class, () -> petRepository.save(cat));
    }

    @ParameterizedTest
    @DisplayName("Should validate correct names")
    @ValueSource(strings = {"abc", "John Doe", "Ириска"})
    void testNameValidation_Valid(String validName) {
        Cat cat = new Cat(validName, LocalDate.of(2020, 1, 1), true, List.of(CatDiseases.IBD), CatBreeds.MIXED, newOwner, newVet);
        assertDoesNotThrow(() -> petRepository.save(cat));
    }

    @Test
    void testDateOfBirthValidation_PastDate() {
        Cat cat = new Cat("Valid Name", LocalDate.of(2000, 1, 1), true, List.of(CatDiseases.IBD), CatBreeds.MIXED, newOwner, newVet);
        assertDoesNotThrow(() -> petRepository.save(cat));
    }

    @Test
    void testDateOfBirthValidation_FutureDate() {
        Cat cat = new Cat("Valid Name", LocalDate.of(3000, 1, 1), true, List.of(CatDiseases.IBD), CatBreeds.MIXED, newOwner, newVet);
        assertThrows(ConstraintViolationException.class, () -> petRepository.save(cat));
    }

    @Test
    void testDateOfBirthValidation_NullDate() {
        Cat cat = new Cat("Valid Name", null, true, List.of(CatDiseases.IBD), CatBreeds.MIXED, newOwner, newVet);
        assertThrows(ConstraintViolationException.class, () -> petRepository.save(cat));
    }

    @Test
    void testChronicDiseasesValidation_Null() {
        Cat cat = new Cat("Valid Name", LocalDate.of(2000, 1, 1), true, null, CatBreeds.MIXED, newOwner, newVet);
        assertThrows(ConstraintViolationException.class, () -> petRepository.save(cat));
    }

    @Test
    void testChronicDiseasesValidation_Empty() {
        Cat cat = new Cat("Valid Name", LocalDate.of(2000, 1, 1), true, Collections.emptyList(), CatBreeds.MIXED, newOwner, newVet);
        assertThrows(ConstraintViolationException.class, () -> petRepository.save(cat));
    }

    @Test
    void testChronicDiseasesValidation_NotEmpty() {
        Cat cat = new Cat("Valid Name", LocalDate.of(2000, 1, 1), true, List.of(CatDiseases.IBD), CatBreeds.MIXED, newOwner, newVet);
        assertDoesNotThrow(() -> petRepository.save(cat));
    }


}