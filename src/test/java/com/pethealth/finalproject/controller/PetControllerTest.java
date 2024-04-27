package com.pethealth.finalproject.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pethealth.finalproject.model.*;
import com.pethealth.finalproject.repository.PetRepository;
import com.pethealth.finalproject.security.repositories.UserRepository;
import com.pethealth.finalproject.service.PetService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
class PetControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PetService petService;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;
    private Cat newCat;
    private Dog newDog;
    private Owner newOwner;
    private Veterinarian newVet;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        LocalDate dateOfBirth = LocalDate.of(2010,06,01);
        newCat = new Cat("Níobe", dateOfBirth, true, List.of(CatDiseases.IBD), CatBreeds.MIXED, null, null);
        newDog = new Dog("Bombo", LocalDate.of(2000, 01, 01), false, List.of(DogDiseases.ARTHRITIS), DogBreeds.HUSKY, null, null);
        newOwner = new Owner("New Owner", "new_owner", "1234", new ArrayList<>(), "owner@mail.com");
        userRepository.save(newOwner);
        newVet = new Veterinarian("New Vet", "new_vet", "0000",  new ArrayList<>(), "vet@mail.com");
        userRepository.save(newVet);
    }

    @AfterEach
    void tearDown() {
        petRepository.deleteAll();
    }

    @Test
    void findAllPets() throws Exception {
        newCat.setOwner(newOwner);
        newDog.setOwner(newOwner);
        petRepository.save(newCat);
        petRepository.save(newDog);
        assertNotNull(newDog);
        MvcResult mvcResult = mockMvc.perform(get("/api/pets"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Níobe"))
//                .andExpect(jsonPath("$[1].name").value("Bombo"))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        JsonNode responseNode = objectMapper.readTree(jsonResponse);
        System.out.println(jsonResponse);
        assertTrue(jsonResponse.contains("Bombo"));
    }

    @Test
    void findPetById() {
    }

    @Test
    void addNewPet() {
    }

    @Test
    void updatePets() {
    }

    @Test
    void patchPets() {
    }

    @Test
    void deletePet() {
    }

    @Test
    void assignVeterinarianToPet() {
    }

    @Test
    void removeVeterinarianFromPet() {
    }
}