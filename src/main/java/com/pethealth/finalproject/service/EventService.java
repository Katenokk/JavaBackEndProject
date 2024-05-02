package com.pethealth.finalproject.service;

import com.pethealth.finalproject.model.Event;
import com.pethealth.finalproject.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    public void addEventToPet(Long petId, Event event) {
        eventRepository.save(event);
    }
}
