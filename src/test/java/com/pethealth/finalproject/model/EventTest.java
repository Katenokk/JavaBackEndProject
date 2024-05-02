package com.pethealth.finalproject.model;

import com.pethealth.finalproject.repository.EventRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EventTest {

    @Autowired
    private EventRepository eventRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }



    @Test
    void testEventCreation() {
        Event event = new Event(LocalDate.now(), "Event");
        assertEquals("Event", event.getComment());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2022-01-01", "2022-01-02", "2022-01-03"})
    void testEventDate(LocalDate date) {
        Event event = new Event(date, "Event");
        assertEquals(date, event.getDate());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2025-01-01", "2025-01-02", "2025-01-03"})
    void testInvalidEventDate(String stringDate) {
        LocalDate date = LocalDate.parse(stringDate);
        Event event = new Event(date, "Event");
        assertThrows(ConstraintViolationException.class, () -> eventRepository.save(event));
    }
}