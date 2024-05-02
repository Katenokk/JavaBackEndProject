package com.pethealth.finalproject.controller;

import com.pethealth.finalproject.dtos.HealthRecordDTO;
import com.pethealth.finalproject.model.HealthRecord;
import com.pethealth.finalproject.model.Weight;
import com.pethealth.finalproject.service.HealthRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/health-records")
public class HealthRecordController {

    @Autowired
    private HealthRecordService healthRecordService;

    @PostMapping("weights/{petId}")
    public ResponseEntity<HealthRecord> addWeightToPet(@PathVariable Long petId,
                                                        @RequestParam LocalDate date,
                                                        @RequestParam double weightInKg) {
        healthRecordService.addWeightToPet(petId, date, weightInKg);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{petId}")
    public ResponseEntity<HealthRecordDTO> getPetHealthRecord(@PathVariable Long petId) {
        return ResponseEntity.ok(healthRecordService.getPetHealthRecord(petId));
    }

    @DeleteMapping("/weights/{weightId}/{petId}")
    public void removeWeightFromPet(@PathVariable Long weightId, @PathVariable Long petId) {
        healthRecordService.removeWeightFromPet(weightId, petId);
    }

    @GetMapping("/weights/{petId}")
    public List<Weight> findWeightsBetweenDates(@PathVariable Long petId,
                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return healthRecordService.findWeightsBetweenDates(petId, startDate, endDate);
    }
}
