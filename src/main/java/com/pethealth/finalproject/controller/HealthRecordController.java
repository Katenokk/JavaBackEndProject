package com.pethealth.finalproject.controller;

import com.pethealth.finalproject.dtos.EventDTO;
import com.pethealth.finalproject.dtos.HealthRecordDTO;
import com.pethealth.finalproject.model.Event;
import com.pethealth.finalproject.model.HealthRecord;
import com.pethealth.finalproject.model.Weight;
import com.pethealth.finalproject.service.EventService;
import com.pethealth.finalproject.service.HealthRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/health-records")
public class HealthRecordController {

    @Autowired
    private HealthRecordService healthRecordService;

    @Autowired
    private EventService eventService;

    @PostMapping("weights/{petId}")
    public ResponseEntity<String> addWeightToPet(@PathVariable Long petId,
                                                        @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd HH:mm") Date date,
                                                        @RequestParam double weightInKg) {
        healthRecordService.addWeightToPet(petId, date, weightInKg);
        return ResponseEntity.ok("Weight added to pet");
    }

    @PostMapping("events/{petId}")
    public Event addEventToPet(@PathVariable Long petId, @RequestBody Event event) {
        return eventService.addEventToPet(petId, event);
    }

    @PatchMapping("events/{eventId}")
    public void partialUpdateEvent(@PathVariable Long eventId, @RequestBody EventDTO eventDto) {
        eventService.partialUpdateEvent(eventId, eventDto);
    }

    @GetMapping("/{petId}")
    public ResponseEntity<HealthRecordDTO> getPetHealthRecord(@PathVariable Long petId) {
        return ResponseEntity.ok(healthRecordService.getPetHealthRecord(petId));
    }

    @GetMapping("events/{petId}")
    public ResponseEntity<List<Event>> getEvents(@PathVariable Long petId) {
        return ResponseEntity.ok(eventService.findEventsByPet(petId));
    }

    @PutMapping("events/{eventId}")
    public ResponseEntity<String> updateEvent(@PathVariable Long eventId, @RequestBody Event event){
        Event updatedEvent = eventService.updateEvent(eventId, event);
        return ResponseEntity.ok("Event updated successfully");
    }

    @DeleteMapping("/weights/{weightId}/{petId}")
    public  ResponseEntity<String> removeWeightFromPet(@PathVariable Long weightId, @PathVariable Long petId) {
        healthRecordService.removeWeightFromPet(weightId, petId);
        return ResponseEntity.ok("Weight removed from pet");
    }

    @GetMapping("/weights/{petId}")
    public List<Weight> findWeightsBetweenDates(@PathVariable Long petId,
                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        return healthRecordService.findWeightsBetweenDates(petId, startDate, endDate);
    }

    @DeleteMapping("/events/{eventId}")
    public  ResponseEntity<String> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.ok("Event deleted successfully");
    }
}
