package com.pethealth.finalproject.model;

import com.pethealth.finalproject.repository.PetRepository;
import com.pethealth.finalproject.security.repositories.UserRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DogTest {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    private Dog newDog;

    private Owner newOwner;
    private Veterinarian newVet;

    @BeforeEach
    void setUp() {
        LocalDate dateOfBirth = LocalDate.of(2000,01,01);
        Date dateOfBirthOld = Date.from(dateOfBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        newDog = new Dog("Bombo", dateOfBirthOld, false, List.of(DogDiseases.ARTHRITIS), DogBreeds.HUSKY, null, null);
        newOwner = new Owner("New Owner", "new_owner", "1234",  new ArrayList<>(), "owner@mail.com");
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
    void dogCreationTest(){
        assertNotNull(newDog);
        assertEquals("Bombo", newDog.getName());
    }

    @Test
    void assignOwner(){
        newDog.setOwner(newOwner);
        newOwner.addPet(newDog);
        petRepository.save(newDog);
        assertEquals("New Owner", newDog.getOwner().getName());
        assertEquals(newOwner, newDog.getOwner());
        assertFalse(newOwner.getOwnedPets().isEmpty());
        assertTrue(newOwner.getOwnedPets().contains(newDog));
    }

    @Test
    void assignVeterinarian(){
        newDog.setVeterinarian(newVet);
        newDog.setOwner(newOwner);
        newVet.addPet(newDog);
        petRepository.save(newDog);
        assertEquals("New Vet", newDog.getVeterinarian().getName());
        assertEquals(newVet, newDog.getVeterinarian());
        assertFalse(newVet.getTreatedPets().isEmpty());
        assertTrue(newVet.getTreatedPets().contains(newDog));
    }

    @Test
    void testChronicDiseasesValidation_Null_Dog() {
        LocalDate dateOfBirth = LocalDate.of(2000,01,01);
        Date dateOfBirthOld = Date.from(dateOfBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        Dog dog = new Dog("Valid Name", dateOfBirthOld, true, null, DogBreeds.MIXED, newOwner, newVet);
        assertThrows(ConstraintViolationException.class, () -> petRepository.save(dog));
    }

    @Test
    void testChronicDiseasesValidation_Empty_Dog() {
        LocalDate dateOfBirth = LocalDate.of(2000,01,01);
        Date dateOfBirthOld = Date.from(dateOfBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        Dog dog = new Dog("Valid Name", dateOfBirthOld, true, Collections.emptyList(), DogBreeds.MIXED, newOwner, newVet);
        assertThrows(ConstraintViolationException.class, () -> petRepository.save(dog));
    }

    @Test
    void testChronicDiseasesValidation_NotEmpty_Dog() {
        LocalDate dateOfBirth = LocalDate.of(2000,01,01);
        Date dateOfBirthOld = Date.from(dateOfBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        Dog dog = new Dog("Valid Name", dateOfBirthOld, true, List.of(DogDiseases.KIDNEY_DISEASE), DogBreeds.MIXED, newOwner, newVet);
        assertDoesNotThrow(() -> petRepository.save(dog));
    }
}