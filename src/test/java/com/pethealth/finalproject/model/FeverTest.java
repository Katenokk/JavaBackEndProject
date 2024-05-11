package com.pethealth.finalproject.model;

import com.pethealth.finalproject.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FeverTest {
    @Autowired
    private EventRepository eventRepository;

    @Test
    void testFeverCreation() {
        LocalDate localDate1 = LocalDate.of(2010,06,01);
        Date date1 = Date.from(localDate1.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        Fever fever = new Fever(date1, "Fever", 39.5);
        assertEquals("Fever", fever.getComment());
        assertEquals(39.5, fever.getDegrees());
    }

}