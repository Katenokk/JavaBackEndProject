package com.pethealth.finalproject.controller;

import com.pethealth.finalproject.dtos.HealthRecordDTO;
import com.pethealth.finalproject.model.HealthRecord;
import com.pethealth.finalproject.service.HealthRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/health-records")
public class HealthRecordController {

    @Autowired
    private HealthRecordService healthRecordService;

    @PostMapping("weights/{healthRecordId}")
    public ResponseEntity<HealthRecord> addWeightToHealthRecord(@PathVariable Long healthRecordId,
                                                        @RequestParam LocalDate date,
                                                        @RequestParam double weightInKg) {
        healthRecordService.addWeightToPet(healthRecordId, date, weightInKg);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{petId}")
    public ResponseEntity<HealthRecordDTO> getPetHealthRecord(@PathVariable Long petId) {
        return ResponseEntity.ok(healthRecordService.getPetHealthRecord(petId));
    }


}
