package com.pethealth.finalproject.security.models;

import com.pethealth.finalproject.model.Admin;
import com.pethealth.finalproject.model.Owner;
import com.pethealth.finalproject.model.Veterinarian;
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
        assertTrue(newOwner instanceof Owner);//siempre es true :(
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

    @ParameterizedTest
    @DisplayName("Should validate correct names")
    @ValueSource(strings = {"Beatriz Lopez", "Laia Fernandez", "John Doe"})
    void testNameValidation_Valid(String validName) {
      User user = new User(validName, "username", "password", new HashSet<>());
      User savedUser = userRepository.save(user);
      assertEquals(validName, savedUser.getName());
    }

    @ParameterizedTest
    @DisplayName("Should validate incorrect names")
    @ValueSource(strings = {"", "A", "12", "John Doe 123"})
    void testNameValidation_Invalid(String invalidName) {
      User user = new User(invalidName, "username", "password", new HashSet<>());
      assertThrows(ConstraintViolationException.class, () -> userRepository.save(user));
    }

    @ParameterizedTest
    @DisplayName("Should validate correct usernames")
    @ValueSource(strings = {"username", "user_name", "User123", "user.name"})
    void testUsernameValidation_Valid(String validUsername) {
        User user = new User("Name", validUsername, "password", new HashSet<>());
        User savedUser = userRepository.save(user);
        assertEquals(validUsername, savedUser.getUsername());
    }

    @ParameterizedTest
    @DisplayName("Should validate incorrect usernames")
    @ValueSource(strings = {"", "us", "username_with_more_than_twenty_characters", "user@name"})
    void testUsernameValidation_Invalid(String invalidUsername) {
        User user = new User("Name", invalidUsername, "password", new HashSet<>());
        assertThrows(ConstraintViolationException.class, () -> userRepository.save(user));
    }

    @ParameterizedTest
    @DisplayName("Should validate correct passwords")
    @ValueSource(strings = {"password", "12345678", "pass1234", "password123"})
    void testPasswordValidation_Valid(String validPassword) {
        User user = new User("Name", "username", validPassword, new HashSet<>());
        User savedUser = userRepository.save(user);
        assertEquals(validPassword, savedUser.getPassword());
    }

    @ParameterizedTest
    @DisplayName("Should validate incorrect passwords")
    @ValueSource(strings = {"", "123", "pas"})
    void testPasswordValidation_Invalid(String invalidPassword) {
        User user = new User("Name", "username", invalidPassword, new HashSet<>());
        assertThrows(ConstraintViolationException.class, () -> userRepository.save(user));
    }

}