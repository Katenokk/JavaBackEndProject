package com.pethealth.finalproject.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Date;

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
        LocalDate localDate1 = LocalDate.of(2010,06,01);
        Date date1 = Date.from(localDate1.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        Vomit vomit = new Vomit(date1, "Vomit", true, true);
        assertEquals("Vomit", vomit.getComment());
        assertTrue(vomit.isHasFood());
        assertTrue(vomit.isHasHairball());
    }

    @Test
    void testAssociationWithHealthRecord() {
        LocalDate localDate1 = LocalDate.of(2010,06,01);
        Date date1 = Date.from(localDate1.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        HealthRecord healthRecord = new HealthRecord();
        Vomit vomit = new Vomit(date1, "Vomit", true, true);
        vomit.setPetHealthRecord(healthRecord);
        assertEquals(healthRecord, vomit.getPetHealthRecord());
    }
}