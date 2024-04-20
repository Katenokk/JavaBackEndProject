package com.pethealth.finalproject.security.models;

import com.pethealth.finalproject.model.Admin;
import com.pethealth.finalproject.model.Owner;
import com.pethealth.finalproject.model.Veterinarian;
import com.pethealth.finalproject.security.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserTest {

    @Autowired
    private UserRepository userRepository;

    private Owner newOwner;
    private Veterinarian newVet;
    private Admin newAdmin;

    @BeforeEach
    void setUp() {
        newOwner = new Owner("Katia", "katia", "1234", new ArrayList<>(), "katia@mail.com");
        newVet = new Veterinarian("Laia Fernández", "laia", "1234", new ArrayList<>(), "laia@pethealth.com");
        newAdmin = new Admin("Admin admin", "admin", "0000", new ArrayList<>());
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void ownerCreationTest() {
        userRepository.save(newOwner);
        assertNotNull(newOwner);
        assertEquals("Katia", newOwner.getName());
    }

    @Test
    void veterinarianCreationTest(){
        userRepository.save(newVet);
        assertNotNull(newVet);
        assertEquals("laia", newVet.getUsername());
    }

    @Test
    void adminCreationTest(){
        userRepository.save(newAdmin);
        assertNotNull(newAdmin);
        assertEquals("admin", newAdmin.getUsername());
    }
}