package com.pethealth.finalproject.security.repositories;

import com.pethealth.finalproject.model.*;
import com.pethealth.finalproject.repository.PetRepository;
import com.pethealth.finalproject.security.models.User;
import com.pethealth.finalproject.security.services.impl.UserService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PetRepository petRepository;

    @Autowired
    private EntityManager entityManager;

    private User testUser;

    private Owner newOwner;

    private Veterinarian newVet;

    private Admin newAdmin;

    @BeforeEach
    void setUp() {
        testUser = new User("Test User", "test-user", "1234", new ArrayList<>());
        newOwner = new Owner("Pepe", "pepito", "0000", new ArrayList<>(), "email@email.com");
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
        Date dateOfBirthOld = Date.from(dateOfBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        Cat newCat = new Cat("Níobe", dateOfBirthOld, true, List.of(CatDiseases.IBD), CatBreeds.MIXED, null, null);
        newVet.addPet(newCat);
        userRepository.save(newVet);
        petRepository.save(newCat);
        assertTrue(newVet.getTreatedPets().contains(newCat));
        userRepository.removeAssociationVeterinarianWithPet(newVet);
        entityManager.clear();
        Cat fromRepoCat = (Cat) petRepository.findById(newCat.getId()).get();
        assertNull(fromRepoCat.getVeterinarian());
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
        assertEquals(newVet.getName(), retrievedVet.get().getName());
    }

    @Test
    void findByEmail() {
        userRepository.save(newOwner);
        Optional<User> retrievedOwner = userRepository.findByEmail(newOwner.getEmail());
        assertTrue(retrievedOwner.isPresent());
        assertEquals(newOwner.getName(), retrievedOwner.get().getName());
    }

    @Test
    void findByIdAndFetchPetsEagerly() {
        Veterinarian testVet1 = new Veterinarian("Dr Test", "test-vet", "1234", new ArrayList(), "email1@mail.com");
        Owner testOner1 = new Owner("Test Owner", "test-owner", "1234", new ArrayList(), "email2@mail.com");
        Cat testCat1 = new Cat("Test Cat", new Date(), true, List.of(CatDiseases.IBD), CatBreeds.MIXED, testOner1, testVet1);
        Dog testDog1 = new Dog("Test Dog", new Date(), true, List.of(DogDiseases.ARTHRITIS), DogBreeds.MIXED, testOner1, testVet1);
        testVet1.addPet(testCat1);
        testVet1.addPet(testDog1);
        userRepository.save(testVet1);
        userRepository.save(testOner1);
        petRepository.save(testCat1);
        petRepository.save(testDog1);

        Optional<Veterinarian> retrievedVet = userRepository.findByIdAndFetchPetsEagerly(testVet1.getId());

        assertTrue(retrievedVet.isPresent());
        assertEquals(retrievedVet.get(), testVet1);
        assertTrue(retrievedVet.get().getTreatedPets().contains(testCat1));
        assertTrue(retrievedVet.get().getTreatedPets().contains(testDog1));
    }


}