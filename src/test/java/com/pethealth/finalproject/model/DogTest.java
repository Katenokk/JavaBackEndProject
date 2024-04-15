package com.pethealth.finalproject.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DogTest {

    private Dog newDog;

    @BeforeEach
    void setUp() {
        newDog = new Dog("Bombo", LocalDate.of(2000, 01, 01), false, List.of(DogDiseases.ARTHRITIS), DogBreeds.HUSKY, null);
    }

    @Test
    void dogCreationTest(){
        assertNotNull(newDog);
        assertEquals("Bombo", newDog.getName());
    }
}