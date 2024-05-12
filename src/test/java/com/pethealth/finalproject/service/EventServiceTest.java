package com.pethealth.finalproject.service;

import com.pethealth.finalproject.dtos.VomitDTO;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
        petRepository.deleteAll();
    }

    @Test
    @Transactional
    void testAddEventToPet_Valid(){
        Owner addEventOwner = new Owner("Add event", "add-owner", "1234", new ArrayList<>(), "addowner@mail.com");
        userRepository.save(addEventOwner);
        LocalDate dateOfBirth = LocalDate.of(200, 1, 1);
        Date dateOfBirthOld = Date.from(dateOfBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        Cat michi = new Cat("Michi", dateOfBirthOld, false, List.of(CatDiseases.IBD), CatBreeds.BENGAL, addEventOwner, vet);
        petRepository.save(michi);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2024);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 30);
        Date date1 = calendar.getTime();

        Event vomit1 = new Vomit(date1, "Vomit", true, false);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        org.springframework.security.core.userdetails.User mockUser = new  org.springframework.security.core.userdetails.User(addEventOwner.getUsername(), addEventOwner.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null, authorities));

        eventService.addEventToPet(michi.getId(), vomit1);

        Cat fromRepoCat = (Cat) petRepository.findById(michi.getId()).get();
        HealthRecord fromRepoHealthRecord = fromRepoCat.getHealthRecord();
        assertTrue(fromRepoHealthRecord.getEvents().contains(vomit1));

        SecurityContextHolder.clearContext();
    }

    @Test
    @Transactional
    void testFindEventsByPet(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2024);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 30);
        Date date2 = calendar.getTime();

        Event vomit2 = new Vomit(date2, "Vomit", true, false);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        org.springframework.security.core.userdetails.User mockUser = new  org.springframework.security.core.userdetails.User(owner.getUsername(), owner.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null, authorities));

        catto.getHealthRecord().addEvent(vomit2);
        eventRepository.save(vomit2);

        List<Event> events = eventService.findEventsByPet(catto.getId());
        assertTrue(events.contains(vomit2));

        SecurityContextHolder.clearContext();
    }

    @Test
    void testUpdateEvent_Valid(){
        Owner updateEventOwner = new Owner("Update event", "update-owner", "1234", new ArrayList<>(), "updateowner@mail.com");
        userRepository.save(updateEventOwner);
        LocalDate dateOfBirth = LocalDate.of(200, 1, 1);
        Date dateOfBirthOld = Date.from(dateOfBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        Cat kitty = new Cat("Kitty", dateOfBirthOld, false, List.of(CatDiseases.IBD), CatBreeds.BENGAL, updateEventOwner, vet);
        petRepository.save(kitty);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2024);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 30);
        Date date3 = calendar.getTime();

        Event vomit3 = new Vomit(date3, "Vomit", true, false);

        kitty.getHealthRecord().addEvent(vomit3);
        eventRepository.save(vomit3);

        Vomit newVomit = new Vomit(date3, "updated", false, true);
        newVomit.setPetHealthRecord(vomit3.getPetHealthRecord());

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        org.springframework.security.core.userdetails.User mockUser = new  org.springframework.security.core.userdetails.User(updateEventOwner.getUsername(), updateEventOwner.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null, authorities));

        eventService.updateEvent(vomit3.getId(), newVomit);

        Optional<Event> fromRepoEvent = eventRepository.findById(vomit3.getId());

        assertTrue(fromRepoEvent.isPresent());
        assertEquals("updated", fromRepoEvent.get().getComment());
        assertTrue(((Vomit) fromRepoEvent.get()).isHasHairball());
        assertFalse(((Vomit) fromRepoEvent.get()).isHasFood());

        SecurityContextHolder.clearContext();
    }

    @Test
    void testPartialUpdateEvent_Valid() {
        //para evitar interferencias con otros tests que usan Owner
//        Owner patchEventOwner = new Owner("Patch event", "patch-owner", "1234", new ArrayList<>(), "patchowner@mail.com");
//        userRepository.save(patchEventOwner);
//        LocalDate dateOfBirth = LocalDate.of(200, 1, 1);
//        Date dateOfBirthOld = Date.from(dateOfBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
//        Cat pipu = new Cat("Pipu", dateOfBirthOld, false, List.of(CatDiseases.IBD), CatBreeds.BENGAL, patchEventOwner, vet);
//        petRepository.save(pipu);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2023);
        calendar.set(Calendar.MONTH, Calendar.MARCH);
        calendar.set(Calendar.DAY_OF_MONTH, 10);
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 35);
        Date date6 = calendar.getTime();

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.YEAR, 2023);
        calendar2.set(Calendar.MONTH, Calendar.MARCH);
        calendar2.set(Calendar.DAY_OF_MONTH, 15);
        calendar2.set(Calendar.HOUR_OF_DAY, 12);
        calendar2.set(Calendar.MINUTE, 35);
        Date date7 = calendar.getTime();

        Event existingEvent = new Vomit(date7, "Vomit", true, false);
        catto.getHealthRecord().addEvent(existingEvent);
        eventRepository.save(existingEvent);

        VomitDTO patchVomit = new VomitDTO();
        patchVomit.setComment("patched");
        patchVomit.setDate(date6);
        patchVomit.setHasFood(false);
        patchVomit.setHasHairball(true);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        org.springframework.security.core.userdetails.User mockUser = new  org.springframework.security.core.userdetails.User(owner.getUsername(), owner.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null, authorities));

        eventService.partialUpdateEvent(existingEvent.getId(), patchVomit);

        Optional<Event> fromRepoEvent = eventRepository.findById(existingEvent.getId());
        assertTrue(fromRepoEvent.isPresent());
        Vomit vomitEvent = (Vomit) fromRepoEvent.get();
        assertEquals("patched", fromRepoEvent.get().getComment());
        assertEquals(date6, fromRepoEvent.get().getDate());
        assertFalse(vomitEvent.isHasFood());
        assertTrue(vomitEvent.isHasHairball());

        SecurityContextHolder.clearContext();
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

        Owner differentOwner = new Owner("Different Owner", "diff-owner", "1234", new ArrayList<>(), "diffowner@mail.com");
        userRepository.save(differentOwner);
        catto.setOwner(differentOwner);
        petRepository.save(catto);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        org.springframework.security.core.userdetails.User mockUser = new  org.springframework.security.core.userdetails.User(differentOwner.getUsername(), differentOwner.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null, authorities));

        Event vomit4 = new Vomit(date4, "Vomit", true, false);

        catto.getHealthRecord().addEvent(vomit4);
        eventRepository.save(vomit4);

        assertTrue(eventRepository.findById(vomit4.getId()).isPresent());
        //assert that vomit4 was added to pet
        Cat fromRepoCat = (Cat) petRepository.findByIdAndFetchEventsEagerly(catto.getId()).get();
        HealthRecord fromRepoHealthRecord = fromRepoCat.getHealthRecord();
        assertTrue(fromRepoHealthRecord.getEvents().contains(vomit4));

        eventService.deleteEvent(vomit4.getId());

        Optional<Event> fromRepoEvent = eventRepository.findById(vomit4.getId());

        assertFalse(fromRepoEvent.isPresent());

        SecurityContextHolder.clearContext();
    }
}