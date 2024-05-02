package com.pethealth.finalproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pethealth.finalproject.model.*;
import com.pethealth.finalproject.repository.HealthRecordRepository;
import com.pethealth.finalproject.repository.PetRepository;
import com.pethealth.finalproject.repository.WeightRepository;
import com.pethealth.finalproject.security.repositories.UserRepository;
import com.pethealth.finalproject.service.PetService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class HealthRecordControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PetService petService;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HealthRecordRepository healthRecordRepository;

    @Autowired
    private WeightRepository weightRepository;
    private Cat catto;
    private Dog newDog;
    private Owner owner;
    private Weight weight1;
    private Weight weight2;
    private HealthRecord healthRecord1;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper.registerModule(new JavaTimeModule());
        owner = new Owner("New Owner", "new-owner", "1234", new ArrayList<>(), "owner@mail.com");
        userRepository.save(owner);
        catto = new Cat("Catto", LocalDate.of(200, 1, 1), false, List.of(CatDiseases.IBD), CatBreeds.BENGAL, owner, null);
        petRepository.save(catto);
        healthRecord1 = new HealthRecord(catto);
        healthRecordRepository.save(healthRecord1);
        weight1 = new Weight( LocalDate.now(), 10.5, healthRecord1);
        weight2= new Weight( LocalDate.now(), 11.5, healthRecord1);
    }

    @AfterEach
    void tearDown() {
    }
    @Test
    void testAddWeightToPet() throws Exception {
        Long petId = catto.getId();
        LocalDate date = LocalDate.now();
        double weightInKg = 10.0;

        mockMvc.perform(post("/health-records/weights/" + petId)
                        .param("date", date.toString())
                        .param("weightInKg", String.valueOf(weightInKg))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        //añadir aserciones
    }

    @Test
    void testGetPetHealthRecord()  throws Exception {
        Long petId = catto.getId();
        mockMvc.perform(get("/health-records/" + petId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        //añadir aserciones
    }

    @Test
    void testRemoveWeightFromPet() throws Exception {
        petRepository.save(catto);
        weightRepository.save(weight1);
        Long petId = catto.getId();
        Long weightId = weight1.getId();

        mockMvc.perform(delete("/health-records/weights/" + weightId + "/" + petId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        //aserciones
    }

    //falta get between dates

}