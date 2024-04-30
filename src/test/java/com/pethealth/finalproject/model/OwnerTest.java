package com.pethealth.finalproject.model;

import com.pethealth.finalproject.security.repositories.UserRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

    @ParameterizedTest
    @DisplayName("Should validate incorrect emails")
    @ValueSource(strings = {"", "invalidnemail", "missing@domain", "missingdomain.com"})
    void testEmailValidation_Invalid(String invalidEmail) {
        Owner owner = new Owner("Name", "username", "password", new ArrayList<>(), invalidEmail);
        assertThrows(ConstraintViolationException.class, () -> userRepository.save(owner));
    }
}