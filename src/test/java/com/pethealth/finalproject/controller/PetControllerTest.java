package com.pethealth.finalproject.controller;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        objectMapper.registerModule(new JavaTimeModule());
        newOwner = new Owner("New Owner", "new_owner", "1234", new ArrayList<>(), "owner@mail.com");
        userRepository.save(newOwner);
        newVet = new Veterinarian("New Vet", "new_vet", "0000",  new ArrayList<>(), "vet@mail.com");
        userRepository.save(newVet);
        LocalDate dateOfBirth = LocalDate.of(2010,06,01);
        Date dateOfBirthOld = Date.from(dateOfBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        newCat = new Cat("Níobe", dateOfBirthOld, true, List.of(CatDiseases.IBD), CatBreeds.MIXED, newOwner, null);
        LocalDate dateOfBirth2 = LocalDate.of(2000, 01, 01);
        Date dateOfBirthOld2 = Date.from(dateOfBirth2.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        newDog = new Dog("Bombo", dateOfBirthOld2, false, List.of(DogDiseases.ARTHRITIS), DogBreeds.HUSKY, newOwner, null);
        petRepository.save(newCat);
    }

    @AfterEach
    void tearDown() {
        petRepository.deleteAll();
        userRepository.deleteAll();
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
                .andExpect(jsonPath("$[1].name").value("Bombo"))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        JsonNode responseNode = objectMapper.readTree(jsonResponse);
        assertTrue(jsonResponse.contains("ARTHRITIS"));
    }


    @Test
    @WithMockUser(username = "new_owner", authorities = {"ROLE_USER"})
    void findPetById() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/pets/" + newCat.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Níobe"))
                .andReturn();
    }

    @Test
    @WithMockUser(username = "new_owner", authorities = {"ROLE_USER"})
    void addNewPet_Valid() throws Exception {
        CatDTO newCatDTO = new CatDTO();
        newCatDTO.setOwner(newOwner);
        newCatDTO.setName("Pipu");
        newCatDTO.setSpayedOrNeutered(true);
        LocalDate dateOfBirth = LocalDate.of(2010, 1, 1);
        Date dateOfBirthOld = Date.from(dateOfBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        newCatDTO.setDateOfBirth(dateOfBirthOld);
        newCatDTO.setChronicDiseases(List.of(CatDiseases.IBD));

        String body = objectMapper.writeValueAsString(newCatDTO);
        MvcResult mvcResult = mockMvc.perform(post("/api/pets")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();

        JsonNode createdCatNode = objectMapper.readTree(jsonResponse);

        String createdPetName = createdCatNode.get("name").asText();
        String createdPetDateOfBirth = createdCatNode.get("dateOfBirth").asText();

        Long createdCatId = createdCatNode.get("id").asLong();
        //retrieve new added cat from repository
        Cat createdCat = (Cat) petRepository.findById(createdCatId).get();

        assertNotNull(createdCat);
        assertEquals(createdCat.getName(), createdPetName);
        assertTrue(createdPetDateOfBirth.contains("2010-01-01"));
    }


    @Test
    @WithMockUser(username = "new_owner", authorities = {"ROLE_USER"})
    void addNewPet_ThrowException_ExistingCat() throws Exception {
        CatDTO newCatDTO = new CatDTO();

        newCatDTO.setOwner(newOwner);
        newCatDTO.setName("Níobe");
        newCatDTO.setSpayedOrNeutered(true);
        LocalDate dateOfBirth = LocalDate.of(2010, 1, 1);
        Date dateOfBirthOld = Date.from(dateOfBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        newCatDTO.setDateOfBirth(dateOfBirthOld);
        newCatDTO.setChronicDiseases(List.of(CatDiseases.IBD));

        String body = objectMapper.writeValueAsString(newCatDTO);
        MvcResult mvcResult = mockMvc.perform(post("/api/pets")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity()).andReturn();

    }
    @Test
    @WithMockUser(username = "new_owner", authorities = {"ROLE_USER"})
    void updatePets_Valid() throws Exception {
        Long existingCatId = newCat.getId();

        CatDTO updatedCatDTO = new CatDTO();
        updatedCatDTO.setName("Updated Name");
        updatedCatDTO.setOwner(newOwner);
        updatedCatDTO.setSpayedOrNeutered(false);
        LocalDate dateOfBirth = LocalDate.of(2010, 1, 1);
        Date dateOfBirthOld = Date.from(dateOfBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        updatedCatDTO.setDateOfBirth(dateOfBirthOld);
        updatedCatDTO.setChronicDiseases(List.of(CatDiseases.DIABETES));

        String body = objectMapper.writeValueAsString(updatedCatDTO);
        mockMvc.perform(put("/api/pets/" + existingCatId)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());

        // Retrieve the updated Cat from the repository
        Cat updatedCat = (Cat) petRepository.findById(existingCatId).get();

        assertEquals(updatedCatDTO.getName(), updatedCat.getName());
        assertEquals(updatedCatDTO.getDateOfBirth(), updatedCat.getDateOfBirth());
        assertEquals(updatedCatDTO.isSpayedOrNeutered(), updatedCat.isSpayedOrNeutered());
        assertEquals(updatedCatDTO.isSpayedOrNeutered(), updatedCat.isSpayedOrNeutered());
    }

    @Test
    @WithMockUser(username = "new_owner", authorities = {"ROLE_USER"})
    void updatePets_ThrowException_CatNotFound() throws Exception {
        CatDTO updatedCatDTO = new CatDTO();
        updatedCatDTO.setName("Updated Name");
        updatedCatDTO.setOwner(newOwner);
        updatedCatDTO.setSpayedOrNeutered(false);
        updatedCatDTO.setSpayedOrNeutered(false);
        LocalDate dateOfBirth = LocalDate.of(2011, 1, 1);
        Date dateOfBirthOld = Date.from(dateOfBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        updatedCatDTO.setDateOfBirth(dateOfBirthOld);
        updatedCatDTO.setChronicDiseases(List.of(CatDiseases.DIABETES));

        String body = objectMapper.writeValueAsString(updatedCatDTO);
        mockMvc.perform(put("/api/pets/100")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "new_owner", authorities = {"ROLE_USER"})
    void patchPets_Valid() throws Exception {
        Long existingCatId = newCat.getId();

        CatDTO updatedCatDTO = new CatDTO();
        updatedCatDTO.setName("Updated Name");
        updatedCatDTO.setSpayedOrNeutered(false);

        String body = objectMapper.writeValueAsString(updatedCatDTO);
        mockMvc.perform(patch("/api/pets/" + existingCatId)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());

        Cat updatedCat = (Cat) petRepository.findById(existingCatId).get();

        assertEquals(updatedCatDTO.getName(), updatedCat.getName());
        assertEquals(updatedCatDTO.isSpayedOrNeutered(), updatedCat.isSpayedOrNeutered());

        assertEquals(newCat.getDateOfBirth(), updatedCat.getDateOfBirth());
        assertEquals(newCat.getCatBreed(), updatedCat.getCatBreed());
    }

    @Test
    @WithMockUser(username = "new_owner", authorities = {"ROLE_USER"})
    void patchPets_ThrowsException_CatNotFound() throws Exception {
        CatDTO updatedCatDTO = new CatDTO();
        updatedCatDTO.setName("Updated Name");
        updatedCatDTO.setOwner(newOwner);
        updatedCatDTO.setSpayedOrNeutered(false);
        LocalDate dateOfBirth = LocalDate.of(2011, 1, 1);
        Date dateOfBirthOld = Date.from(dateOfBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        updatedCatDTO.setDateOfBirth(dateOfBirthOld);
        updatedCatDTO.setChronicDiseases(List.of(CatDiseases.DIABETES));

        String body = objectMapper.writeValueAsString(updatedCatDTO);

        mockMvc.perform(patch("/api/pets/100")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "new_owner", authorities = {"ROLE_USER"})
    void deletePet_Valid() throws Exception {
        Long existingCatId = newCat.getId();

        mockMvc.perform(delete("/api/pets/" + existingCatId)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        assertFalse(petRepository.existsById(existingCatId));
    }

    @Test
    @WithMockUser(username = "new_owner", authorities = {"ROLE_USER"})
    void deletePet_ThrowsException_CatNotFound() throws Exception {
        mockMvc.perform(delete("/api/pets/100")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "new_owner", authorities = {"ROLE_USER"})
    void assignVeterinarianToPet_Valid() throws Exception {
        userRepository.save(newVet);
        Long existingCatId = newCat.getId();
        Long existingVetId = newVet.getId();

        mockMvc.perform(patch("/api/pets/veterinarians/" + existingCatId + "/" + existingVetId)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        Cat updatedCat = (Cat) petRepository.findById(existingCatId).get();

        assertEquals(newVet, updatedCat.getVeterinarian());
    }

    @Test
    @WithMockUser(username = "new_owner", authorities = {"ROLE_USER"})
    void assignVeterinarianToPet_CatNotFound() throws Exception {
        userRepository.save(newVet);
        Long existingVetId = newVet.getId();

        mockMvc.perform(patch("/api/pets/veterinarians/100/" + existingVetId)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "new_owner", authorities = {"ROLE_USER"})
    void assignVeterinarianToPet_VeterinaryNotFound() throws Exception {
        Long existingCatId = newCat.getId();

        mockMvc.perform(patch("/api/pets/veterinarians/" + existingCatId + "/100")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "new_owner", authorities = {"ROLE_USER"})
    void removeVeterinarianFromPet_Valid() throws Exception {
        userRepository.save(newVet);
        Long existingCatId = newCat.getId();
        Long existingVetId = newVet.getId();

        petService.addVeterinarianToPet(existingCatId, existingVetId);

        mockMvc.perform(delete("/api/pets/veterinarians/" + existingCatId + "/" + existingVetId)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        Cat updatedCat = (Cat) petRepository.findById(existingCatId).get();

        // Assert that the Veterinarian is not assigned to the Cat anymore
        assertNotEquals(newVet, updatedCat.getVeterinarian());
    }

    @Test
    @WithMockUser(username = "new_owner", authorities = {"ROLE_USER"})
    void removeVeterinarianFromPet_CatNotFound() throws Exception {
        userRepository.save(newVet);
        Long existingVetId = newVet.getId();

        mockMvc.perform(delete("/api/pets/veterinarians/100/" + existingVetId)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "new_owner", authorities = {"ROLE_USER"})
    void removeVeterinarianFromPet_VeterinarianNotFound() throws Exception {
        Long existingCatId = newCat.getId();

        mockMvc.perform(delete("/api/pets/veterinarians/" + existingCatId + "/100")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }
}