package com.pethealth.finalproject.model;

import com.pethealth.finalproject.repository.EventRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.stream.Stream;

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
        Event event = new Event(date1, "Event");
        assertEquals("Event", event.getComment());
    }


    @ParameterizedTest
    @ValueSource(strings = {"2022-01-01 12:00", "2022-01-02 13:00", "2022-01-03 08:00"})
    void testEventDate(String stringDate) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = formatter.parse(stringDate);

        Event event = new Event(date, "Event");
        assertEquals(date, event.getDate());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2025-01-01 13:30", "2025-01-02 12:00", "2025-01-03 19:00"})
    void testInvalidEventDate(String stringDate) throws ParseException{
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = formatter.parse(stringDate);

        Event event = new Event(date, "Event");
        assertThrows(ConstraintViolationException.class, () -> eventRepository.save(event));
    }

    @ParameterizedTest
    @MethodSource("provideCommentsForTesting")
    void testEventComment(String comment) {
        Event event = new Event(date1, comment);

        if (comment.length() <= 255) {
            assertEquals(comment, event.getComment());
        } else {
            assertThrows(ConstraintViolationException.class, () -> eventRepository.save(event));
        }
    }

    private static Stream<String> provideCommentsForTesting() {
        return Stream.of(
                "This is a short comment",
                String.valueOf(new char[255]).replace("\0", "a"), // a comment of length 255
                String.valueOf(new char[256]).replace("\0", "a")  // a comment of length 256
        );
    }


}