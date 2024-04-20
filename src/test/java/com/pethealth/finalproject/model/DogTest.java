package com.pethealth.finalproject.model;

import com.pethealth.finalproject.repository.PetRepository;
import com.pethealth.finalproject.security.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
        newDog = new Dog("Bombo", LocalDate.of(2000, 01, 01), false, List.of(DogDiseases.ARTHRITIS), DogBreeds.HUSKY, null, null);
        newOwner = new Owner("New Owner", "new_owner", "1234",  new HashSet<>(), "owner@mail.com");
        userRepository.save(newOwner);
        newVet = new Veterinarian("New Vet", "new_vet", "0000",   new HashSet<>(), "vet@mail.com");
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
}