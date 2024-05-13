package com.pethealth.finalproject.service;

import com.pethealth.finalproject.dtos.*;
import com.pethealth.finalproject.model.*;
import com.pethealth.finalproject.repository.HealthRecordRepository;
import com.pethealth.finalproject.repository.PetRepository;
import com.pethealth.finalproject.repository.WeightRepository;
import com.pethealth.finalproject.security.models.User;
import com.pethealth.finalproject.security.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class HealthRecordService {
        @Autowired
        private PetRepository petRepository;

        @Autowired
        private WeightRepository weightRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private HealthRecordRepository healthRecordRepository;

    public String getCurrentUserName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            return currentUserName;
        }
        return null;
    }


    public  void addWeightToPet(Long petId, Date date, double weightInKg) {
        String currentUserName = getCurrentUserName();
        User currentUser = userRepository.findByUsername(currentUserName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found with id " + petId));

        if (!(currentUser.equals(pet.getOwner()) || currentUser instanceof Admin)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access is denied.");
        }

        HealthRecord healthRecord = pet.getHealthRecord();

        if (healthRecord == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "HealthRecord not found for Pet with id " + petId);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // Change the time zone of the Calendar object to UTC
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        // The Calendar's time is now the same instant in UTC
        Date utcDate = calendar.getTime();

        Weight weight = new Weight(utcDate, weightInKg, healthRecord);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Weight>> violations = validator.validate(weight);

        if(!violations.isEmpty()){
            throw new IllegalArgumentException(violations.toString());
        }
        //
        healthRecord.addWeight(weight);

        weightRepository.saveAndFlush(weight);
    }

    public HealthRecordDTO getPetHealthRecord(Long petId) {
        String currentUserName = getCurrentUserName();
        User currentUser = userRepository.findByUsername(currentUserName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found with id " + petId));


        if (!(currentUser.equals(pet.getOwner()) || currentUser.equals(pet.getVeterinarian()) || currentUser instanceof Admin)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access is denied.");
        }

        HealthRecord healthRecord = pet.getHealthRecord();
        return convertToDTO(healthRecord);
    }

    private HealthRecordDTO convertToDTO(HealthRecord healthRecord) {
        HealthRecordDTO dto = new HealthRecordDTO();
        dto.setId(healthRecord.getId());
        dto.setWeights(healthRecord.getWeights().stream()
                .map(this::convertWeightToDTO)
                .toList());
        dto.setEvents(convertEventListToDTO(healthRecord.getEvents()));
        return dto;
    }

    private WeightDTO convertWeightToDTO(Weight weight) {
        WeightDTO dto = new WeightDTO();
        dto.setId(weight.getId());
        dto.setDay(weight.getDay());
        dto.setWeight(weight.getWeight());
        return dto;
    }

    private List<EventDTO> convertEventListToDTO(List<Event> events) {
        return events.stream()
                .map(this::convertEventToDTO)
                .collect(Collectors.toList());
    }

    private EventDTO convertEventToDTO(Event event) {
        EventDTO dto = new EventDTO();
        if (event instanceof Vomit) {
            VomitDTO vomitDTO = new VomitDTO();
            vomitDTO.setHasFood(((Vomit) event).isHasFood());
            vomitDTO.setHasHairball(((Vomit) event).isHasHairball());
            dto = vomitDTO;
        } else if (event instanceof Fever) {
            FeverDTO feverDTO = new FeverDTO();
            feverDTO.setDegrees(((Fever) event).getDegrees());
            dto = feverDTO;
        } else if (event instanceof Medication) {
            MedicationDTO medicationDTO = new MedicationDTO();
            medicationDTO.setName(((Medication) event).getName());
            medicationDTO.setDosageInMgPerDay(((Medication) event).getDosageInMgPerDay());
            dto = medicationDTO;
        } else {
            dto = new EventDTO();
        }
        dto.setId(event.getId());
        dto.setDate(event.getDate());
        dto.setComment(event.getComment());

        return dto;
    }

    public List<Weight> findWeightsBetweenDates(Long petId, Date startDate, Date endDate) {
        String currentUserName = getCurrentUserName();
        User currentUser = userRepository.findByUsername(currentUserName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found with id " + petId));

        if (!(currentUser.equals(pet.getOwner()) || currentUser.equals(pet.getVeterinarian()) || currentUser instanceof Admin)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access is denied.");
        }

        if(endDate.before(startDate)){
            throw new IllegalArgumentException("End date must be after start date");
        }

        HealthRecord healthRecord = pet.getHealthRecord();
        List<Weight> weights = weightRepository.findAllByHealthRecordAndDayBetween(healthRecord, startDate, endDate);
        if(weights.isEmpty()){
            throw new EntityNotFoundException("No weights found between dates");
        }
        return weights;
    }

    public void removeWeightFromPet(Long weightId, Long petId) {
        String currentUserName = getCurrentUserName();
        User currentUser = userRepository.findByUsername(currentUserName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found with id " + petId));

        if (!(currentUser.equals(pet.getOwner()) || currentUser instanceof Admin)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access is denied.");
        }
        HealthRecord healthRecord = pet.getHealthRecord();
        Weight weight = weightRepository.findById(weightId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Weight not found with id " + weightId));
        healthRecord.removeWeight(weight);
        weightRepository.delete(weight);
    }

}
