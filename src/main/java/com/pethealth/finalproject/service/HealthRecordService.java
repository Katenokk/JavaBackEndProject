package com.pethealth.finalproject.service;

import com.pethealth.finalproject.dtos.HealthRecordDTO;
import com.pethealth.finalproject.dtos.WeightDTO;
import com.pethealth.finalproject.model.HealthRecord;
import com.pethealth.finalproject.model.Pet;
import com.pethealth.finalproject.model.Weight;
import com.pethealth.finalproject.repository.PetRepository;
import com.pethealth.finalproject.repository.WeightRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class HealthRecordService {
        @Autowired
        private PetRepository petRepository;

        @Autowired
        private WeightRepository weightRepository;

//        public void addWeightToPet(Long petId, Weight weight) {
//            Pet pet = petRepository.findById(petId)
//                    .orElseThrow(() -> new EntityNotFoundException("Pet not found with id " + petId));
//
//            HealthRecord healthRecord = pet.getHealthRecord();
//            healthRecord.addWeight(weight);
//
//            weightRepository.save(weight);
//        }

    public  void addWeightToPet(Long petId, Date date, double weightInKg) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found with id " + petId));

        HealthRecord healthRecord = pet.getHealthRecord();
        Weight weight = new Weight(date, weightInKg, healthRecord);
        //
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Weight>> violations = validator.validate(weight);

        if(!violations.isEmpty()){
            throw new IllegalArgumentException(violations.toString());
        }
        //
        healthRecord.addWeight(weight);

        weightRepository.save(weight);
    }

    public HealthRecordDTO getPetHealthRecord(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found with id " + petId));
        HealthRecord healthRecord = pet.getHealthRecord();
        return convertToDTO(healthRecord);
    }

    private HealthRecordDTO convertToDTO(HealthRecord healthRecord) {
        HealthRecordDTO dto = new HealthRecordDTO();
        dto.setId(healthRecord.getId());
        dto.setWeights(healthRecord.getWeights().stream()
                .map(this::convertWeightToDTO)
                .toList());

        return dto;
    }

    private WeightDTO convertWeightToDTO(Weight weight) {
        WeightDTO dto = new WeightDTO();
        dto.setId(weight.getId());
        dto.setDay(weight.getDay());
        dto.setWeight(weight.getWeight());
        return dto;
    }

    public List<Weight> findWeightsBetweenDates(Long petId, Date startDate, Date endDate) {
        if(endDate.before(startDate)){
            throw new IllegalArgumentException("End date must be after start date");
        }
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found with id " + petId));
        HealthRecord healthRecord = pet.getHealthRecord();
        List<Weight> weights = weightRepository.findAllByHealthRecordAndDayBetween(healthRecord, startDate, endDate);
        if(weights.isEmpty()){
            throw new EntityNotFoundException("No weights found between dates");
        }
        return weights;
    }

    //testear luego y añadir delete en controller
    public void removeWeightFromPet(Long weightId, Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found with id " + petId));
        HealthRecord healthRecord = pet.getHealthRecord();
        Weight weight = weightRepository.findById(weightId)
                .orElseThrow(() -> new EntityNotFoundException("Weight not found with id " + weightId));
        healthRecord.removeWeight(weight);
        weightRepository.delete(weight);
    }

}
