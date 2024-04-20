package com.pethealth.finalproject.model;

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
class OwnerTest {

    @Autowired
    private UserRepository userRepository;

    private Owner owner;

    @BeforeEach
    void setUp() {
        owner = new Owner("Katia", "katia", "1234", new HashSet<>(), "katia@mail.com");
        userRepository.save(owner);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void ownerCreationTest(){
        assertNotNull(owner);
        assertEquals("Katia", owner.getName());
    }
}