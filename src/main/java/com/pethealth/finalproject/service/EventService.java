package com.pethealth.finalproject.service;

import com.pethealth.finalproject.model.*;
import com.pethealth.finalproject.repository.EventRepository;
import com.pethealth.finalproject.repository.HealthRecordRepository;
import com.pethealth.finalproject.repository.PetRepository;
import com.pethealth.finalproject.repository.WeightRepository;
import com.pethealth.finalproject.security.models.User;
import com.pethealth.finalproject.security.repositories.UserRepository;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.util.*;
import java.text.ParseException;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.PlatformTransactionManager;



@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private WeightRepository weightRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HealthRecordRepository healthRecordRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    public String getCurrentUserName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            return currentUserName;
        }
        return null;
    }

//    @Transactional
    public Event addEventToPet(Long petId, Event event) {
        String currentUserName = getCurrentUserName();
        User currentUser = userRepository.findByUsername(currentUserName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found with id " + petId));

        if (!(currentUser.equals(pet.getOwner()) || currentUser instanceof Admin)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access is denied.");
        }

        HealthRecord healthRecord = pet.getHealthRecord();
        healthRecord.getEvents().size();

//        Long healthRecordId = pet.getHealthRecord().getId();
//        HealthRecord healthRecord = healthRecordRepository.findByIdAndInitializeEvents(healthRecordId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Health record not found with id " + healthRecordId));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(event.getDate());
        // Change the time zone of the Calendar object to UTC
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        // The Calendar's time is now the same instant in UTC
        Date utcDate = calendar.getTime();
        event.setDate(utcDate);

        healthRecord.addEvent(event);
        event = eventRepository.save(event);

        return event;
    }

    public List<Event> findEventsByPet(Long petId) {
        String currentUserName = getCurrentUserName();
        User currentUser = userRepository.findByUsername(currentUserName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found with id " + petId));


        if (!(currentUser.equals(pet.getOwner()) || currentUser.equals(pet.getVeterinarian()) || currentUser instanceof Admin)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access is denied.");
        }

        return pet.getHealthRecord().getEvents();
    }

    @Transactional
    public Event updateEvent(Long eventId, Event event){
        String currentUserName = getCurrentUserName();
        User currentUser = userRepository.findByUsername(currentUserName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found with id " + eventId));
        if (!existingEvent.getClass().equals(event.getClass())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The type of the provided event does not match the type of the existing event");
        }

        Pet pet = petRepository.findById(existingEvent.getPetHealthRecord().getPet().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found with id " + existingEvent.getPetHealthRecord().getPet().getId()));


        if (!(currentUser.equals(pet.getOwner()) || currentUser instanceof Admin)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access is denied.");
        }

        event.setPetHealthRecord(existingEvent.getPetHealthRecord());
        event.setId(existingEvent.getId());
        return eventRepository.save(event);
    }



    public void deleteEvent(Long eventId) {
        String currentUserName = getCurrentUserName();
        User currentUser = userRepository.findByUsername(currentUserName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found with id " + eventId));

        Pet pet = petRepository.findById(existingEvent.getPetHealthRecord().getPet().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found with id " + existingEvent.getPetHealthRecord().getPet().getId()));


        if (!(currentUser.equals(pet.getOwner()) || currentUser instanceof Admin)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access is denied.");
        }

        HealthRecord healthRecord = healthRecordRepository.findByIdAndInitializeEvents(existingEvent.getPetHealthRecord().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Health record not found with id " + existingEvent.getPetHealthRecord().getId()));
        healthRecord.getEvents().remove(existingEvent);
        healthRecordRepository.save(healthRecord);

        eventRepository.delete(existingEvent);
    }
}
