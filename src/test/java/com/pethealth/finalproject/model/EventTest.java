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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EventTest {

    @Autowired
    private EventRepository eventRepository;

    private Date date1;

    @BeforeEach
    void setUp() {
        LocalDate localDate1 = LocalDate.of(2010,06,01);
        Date date1 = Date.from(localDate1.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
    }

    @AfterEach
    void tearDown() {
    }



    @Test
    void testEventCreation() {
//        LocalDate localDate1 = LocalDate.of(2010,06,01);
//        Date date1 = Date.from(localDate1.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        Event event = new Event(date1, "Event");
        assertEquals("Event", event.getComment());
    }

//    @ParameterizedTest
//    @ValueSource(strings = {"2022-01-01", "2022-01-02", "2022-01-03"})
//    void testEventDate(Date date) {
//        Event event = new Event(date1, "Event");
//        assertEquals(date, event.getDate());
//    }

    @ParameterizedTest //probar con horas cuando arregle la UTC
    @ValueSource(strings = {"2022-01-01", "2022-01-02", "2022-01-03"})
    void testEventDate(String stringDate) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = formatter.parse(stringDate);

        Event event = new Event(date, "Event");
        assertEquals(date, event.getDate());
    }

//    @ParameterizedTest
//    @ValueSource(strings = {"2025-01-01", "2025-01-02", "2025-01-03"})
//    void testInvalidEventDate(String stringDate) {
//        LocalDate date = LocalDate.parse(stringDate);
//        Event event = new Event(date, "Event");
//        assertThrows(ConstraintViolationException.class, () -> eventRepository.save(event));
//    }
}