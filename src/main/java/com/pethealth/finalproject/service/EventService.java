package com.pethealth.finalproject.service;

import com.pethealth.finalproject.model.Event;
import com.pethealth.finalproject.model.HealthRecord;
import com.pethealth.finalproject.model.Pet;
import com.pethealth.finalproject.model.Weight;
import com.pethealth.finalproject.repository.EventRepository;
import com.pethealth.finalproject.repository.HealthRecordRepository;
import com.pethealth.finalproject.repository.PetRepository;
import com.pethealth.finalproject.repository.WeightRepository;
import com.pethealth.finalproject.security.repositories.UserRepository;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.text.ParseException;


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

//    @Transactional
    public Event addEventToPet(Long petId, Event event) {
        Pet pet = petRepository.findByIdAndFetchEventsEagerly(petId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found with id " + petId));

        HealthRecord healthRecord = pet.getHealthRecord();
        healthRecord.getEvents().size();

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
        Pet pet = petRepository.findByIdAndFetchEventsEagerly(petId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found with id " + petId));

        return pet.getHealthRecord().getEvents();
    }

    @Transactional
    public void updateEvent(Long eventId, Event event){
        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found with id " + eventId));
        if (!existingEvent.getClass().equals(event.getClass())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The type of the provided event does not match the type of the existing event");
        }
        event.setPetHealthRecord(existingEvent.getPetHealthRecord());
        event.setId(existingEvent.getId());
        eventRepository.save(event);
    }

    public void deleteEvent(Long eventId) {
        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found with id " + eventId));

        HealthRecord healthRecord = healthRecordRepository.findByIdAndInitializeEvents(existingEvent.getPetHealthRecord().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Health record not found with id " + existingEvent.getPetHealthRecord().getId()));
        healthRecord.getEvents().remove(existingEvent);
        healthRecordRepository.save(healthRecord);

        eventRepository.delete(existingEvent);
    }
}
