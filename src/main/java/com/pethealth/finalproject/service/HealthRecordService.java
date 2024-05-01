package com.pethealth.finalproject.service;

import com.pethealth.finalproject.dtos.HealthRecordDTO;
import com.pethealth.finalproject.dtos.WeightDTO;
import com.pethealth.finalproject.model.HealthRecord;
import com.pethealth.finalproject.model.Pet;
import com.pethealth.finalproject.model.Weight;
import com.pethealth.finalproject.repository.PetRepository;
import com.pethealth.finalproject.repository.WeightRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

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

    public void addWeightToPet(Long petId, LocalDate date, double weightInKg) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found with id " + petId));

        HealthRecord healthRecord = pet.getHealthRecord();
        Weight weight = new Weight(date, weightInKg, healthRecord);
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

    public List<Weight> findWeightsBetweenDates(LocalDate startDate, LocalDate endDate) {
        if(endDate.isBefore(startDate)){
            throw new IllegalArgumentException("End date must be after start date");
        }
        List<Weight> weights = weightRepository.findAllBetweenDates(startDate, endDate);
        if(weights.isEmpty()){
            throw new EntityNotFoundException("No weights found between dates");
        }
        return weights;
    }

}
