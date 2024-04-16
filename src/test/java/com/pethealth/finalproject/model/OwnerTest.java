package com.pethealth.finalproject.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class OwnerTest {

    private Owner owner;

    @BeforeEach
    void setUp() {
        owner = new Owner("Katia", "katia", "1234", new ArrayList<>(), "katia@mail.com");
    }

    @Test
    void ownerCreationTest(){
        assertNotNull(owner);
        assertEquals("Katia", owner.getName());
    }
}