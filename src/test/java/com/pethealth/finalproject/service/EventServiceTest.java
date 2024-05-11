package com.pethealth.finalproject.service;

import com.pethealth.finalproject.model.*;
import com.pethealth.finalproject.repository.EventRepository;
import com.pethealth.finalproject.repository.HealthRecordRepository;
import com.pethealth.finalproject.repository.PetRepository;
import com.pethealth.finalproject.repository.WeightRepository;
import com.pethealth.finalproject.security.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EventServiceTest {

    @Autowired
    private HealthRecordService healthRecordService;
    @Autowired
    private HealthRecordRepository healthRecordRepository;
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private WeightRepository weightRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventService eventService;


    private Weight weight1;
    private Weight weight2;

    private HealthRecord healthRecord1;

    private Cat catto;
    private Owner owner;

    private Veterinarian vet;

    @BeforeEach
    void setUp() {
        owner = new Owner("New Owner", "new-owner", "1234", new ArrayList<>(), "owner@mail.com");
        userRepository.save(owner);
        vet = new Veterinarian("New Vet", "new-vet", "1234", new ArrayList<>(), "vet@mail.com");
        userRepository.save(vet);

        LocalDate dateOfBirth = LocalDate.of(200, 1, 1);
        Date dateOfBirthOld = Date.from(dateOfBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        catto = new Cat("Catto", dateOfBirthOld, false, List.of(CatDiseases.IBD), CatBreeds.BENGAL, owner, vet);
        petRepository.save(catto);


        healthRecord1 = new HealthRecord(catto);
        healthRecordRepository.save(healthRecord1);

        LocalDate now = LocalDate.now();
        Date dateNow = Date.from(now.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        weight1 = new Weight( dateNow, 10.5, healthRecord1);
        weight2= new Weight( dateNow, 11.5, healthRecord1);
    }

    @AfterEach
    void tearDown() {
        weightRepository.deleteAll();
    }

    @Test
    @Transactional
    void testAddEventToPet_Valid(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2024);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 30);
        Date date1 = calendar.getTime();

        Event vomit1 = new Vomit(date1, "Vomit", true, false);
        eventService.addEventToPet(catto.getId(), vomit1);

        Cat fromRepoCat = (Cat) petRepository.findById(catto.getId()).get();
        HealthRecord fromRepoHealthRecord = fromRepoCat.getHealthRecord();
        assertTrue(fromRepoHealthRecord.getEvents().contains(vomit1));
    }

    @Test
    void testFindEventsByPet(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2024);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 30);
        Date date2 = calendar.getTime();

        Event vomit2 = new Vomit(date2, "Vomit", true, false);
        eventService.addEventToPet(catto.getId(), vomit2);

        List<Event> events = eventService.findEventsByPet(catto.getId());
        assertTrue(events.contains(vomit2));
    }

    @Test
    void testUpdateEvent_Valid(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2024);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 30);
        Date date3 = calendar.getTime();

        Event vomit3 = new Vomit(date3, "Vomit", true, false);
        eventService.addEventToPet(catto.getId(), vomit3);

        Vomit newVomit = new Vomit(date3, "updated", false, true);

        eventService.updateEvent(vomit3.getId(), newVomit);

        Optional<Event> fromRepoEvent = eventRepository.findById(vomit3.getId());

        assertTrue(fromRepoEvent.isPresent());
        assertEquals("updated", fromRepoEvent.get().getComment());
        assertTrue(((Vomit) fromRepoEvent.get()).isHasHairball());
        assertFalse(((Vomit) fromRepoEvent.get()).isHasFood());
    }

    @Test
    void testDeleteEvent_Valid(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2024);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 30);
        Date date4 = calendar.getTime();

        Event vomit4 = new Vomit(date4, "Vomit", true, false);
        eventService.addEventToPet(catto.getId(), vomit4);
        assertTrue(eventRepository.findById(vomit4.getId()).isPresent());
        //assert that vomit4 was added to pet
        Cat fromRepoCat = (Cat) petRepository.findByIdAndFetchEventsEagerly(catto.getId()).get();
        HealthRecord fromRepoHealthRecord = fromRepoCat.getHealthRecord();
        assertTrue(fromRepoHealthRecord.getEvents().contains(vomit4));

        eventService.deleteEvent(vomit4.getId());

        Optional<Event> fromRepoEvent = eventRepository.findById(vomit4.getId());

        assertFalse(fromRepoEvent.isPresent());
    }
}