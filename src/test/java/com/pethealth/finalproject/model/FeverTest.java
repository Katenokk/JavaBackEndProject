package com.pethealth.finalproject.model;

import com.pethealth.finalproject.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FeverTest {
    @Autowired
    private EventRepository eventRepository;

    @Test
    void testFeverCreation() {
        Fever fever = new Fever(LocalDate.now(), "Fever", 39.5);
        assertEquals("Fever", fever.getComment());
        assertEquals(39.5, fever.getDegrees());
    }

}