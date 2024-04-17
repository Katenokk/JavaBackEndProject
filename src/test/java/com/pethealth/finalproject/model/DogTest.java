package com.pethealth.finalproject.model;

import com.pethealth.finalproject.repository.PetRepository;
import com.pethealth.finalproject.security.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
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
//        petRepository.save(newDog);
        newOwner = new Owner("New Owner", "new_owner", "1234", new ArrayList<>(), "owner@mail.com");
//        userRepository.save(newOwner);
        newVet = new Veterinarian("New Vet", "new_vet", "0000",  new ArrayList<>(), "vet@mail.com");
    }

    @Test
    void dogCreationTest(){
        assertNotNull(newDog);
        assertEquals("Bombo", newDog.getName());
    }

//    @Test
//    void assignOwner(){
//        petRepository.save(newDog);
//        userRepository.save(newOwner);
//        newDog.setOwner(newOwner);
//        assertEquals("New Owner", newDog.getOwner().getName());
//        assertEquals(newOwner, newDog.getOwner());
//        newOwner.addPet(newDog);
////        assertTrue(newOwner.getPets().contains(newDog));
//
//
//        assertNotNull(newOwner.getPets());
//    }

    @Test
    void assignVeterinarian(){
        newDog.setVeterinarian(newVet);
        assertEquals("New Vet", newDog.getVeterinarian().getName());
        assertEquals(newVet, newDog.getVeterinarian());
    }
}