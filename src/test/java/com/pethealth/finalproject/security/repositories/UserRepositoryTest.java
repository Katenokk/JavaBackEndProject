package com.pethealth.finalproject.security.repositories;

import com.pethealth.finalproject.model.*;
import com.pethealth.finalproject.repository.PetRepository;
import com.pethealth.finalproject.security.models.User;
import com.pethealth.finalproject.security.services.impl.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PetRepository petRepository;

    private User testUser;

    private Owner newOwner;

    private Veterinarian newVet;

    private Admin newAdmin;

    @BeforeEach
    void setUp() {
        testUser = new User("Test User", "test-user", "1234", new ArrayList<>());
        newOwner = new Owner("Pepe", "pepito", "0000", new ArrayList<>(), "email.com");
        newVet = new Veterinarian("Oriol", "dr gato", "1111", new ArrayList<>(), "oriol@email.com");
        newAdmin = new Admin("Admin", "admin", "888", new ArrayList<>());
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void findByUsername() {
        userRepository.save(newOwner);
        Optional<User> retrievedOwner = userRepository.findByUsername(newOwner.getUsername());
        assertTrue(retrievedOwner.isPresent());
        assertEquals(newOwner.getName(), retrievedOwner.get().getName());
    }

    @Test
    @Transactional
    void removeAssociationVeterinarianWithPet() {
        LocalDate dateOfBirth = LocalDate.of(2010,06,01);
        Cat newCat = new Cat("Níobe", dateOfBirth, true, List.of(CatDiseases.IBD), CatBreeds.MIXED, null, null);
        newVet.addPet(newCat);
        userRepository.save(newVet);
        petRepository.save(newCat);
        assertTrue(newVet.getTreatedPets().contains(newCat));
        userRepository.removeAssociationVeterinarianWithPet(newVet);
        assertNull(newCat.getVeterinarian());
    }

    @Test
    void findOwnerByEmail() {
        userRepository.save(newOwner);
        Optional<Owner> retrievedOwner = userRepository.findOwnerByEmail(newOwner.getEmail());
        assertTrue(retrievedOwner.isPresent());
        assertEquals(newOwner.getName(), retrievedOwner.get().getName());
    }

    @Test
    void findVetByEmail() {
        userRepository.save(newVet);
        Optional<Veterinarian> retrievedVet = userRepository.findVetByEmail(newVet.getEmail());
        assertTrue(retrievedVet.isPresent());
        assertEquals(newOwner.getName(), retrievedVet.get().getName());
    }
}