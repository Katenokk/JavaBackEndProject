package com.pethealth.finalproject.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class VomitTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testVomitCreation() {
        Vomit vomit = new Vomit(LocalDate.now(), "Vomit", true, true);
        assertEquals("Vomit", vomit.getComment());
        assertTrue(vomit.isHasFood());
        assertTrue(vomit.isHasHairball());
    }
}