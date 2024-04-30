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
class VeterinarianTest {

    @Autowired
    private UserRepository userRepository;

    private Veterinarian newVet;

    @BeforeEach
    void setUp() {
        newVet = new Veterinarian("Laia", "laia", "1234", new HashSet<>(), "laia@mail.com");
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }
    @Test
    void ownerCreationTest(){
        assertNotNull(newVet);
        assertEquals("Laia", newVet.getName());
    }

    @ParameterizedTest
    @DisplayName("Should validate incorrect emails")
    @ValueSource(strings = {"", "invalidnemail", "missing@domain", "missingdomain.com"})
    void testEmailValidation_Invalid(String invalidEmail) {
        Veterinarian veterinarian = new Veterinarian("Name", "username", "password", new ArrayList<>(), invalidEmail);
        assertThrows(ConstraintViolationException.class, () -> userRepository.save(veterinarian));
    }
}