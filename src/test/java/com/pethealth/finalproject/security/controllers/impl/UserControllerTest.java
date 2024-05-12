package com.pethealth.finalproject.security.controllers.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pethealth.finalproject.model.*;
import com.pethealth.finalproject.repository.PetRepository;
import com.pethealth.finalproject.security.dtos.UserDTO;
import com.pethealth.finalproject.security.dtos.UserUpdateDTO;
import com.pethealth.finalproject.security.models.Role;
import com.pethealth.finalproject.security.models.User;
import com.pethealth.finalproject.security.repositories.UserRepository;
import com.pethealth.finalproject.security.services.impl.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;



@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Cat newCat;
    private Dog newDog;
    private Owner newOwner;
    private Veterinarian newVet;

    private User testUser;

    private Admin newAdmin;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        testUser = new User("Test User", "test-user", "1234", new ArrayList<>());
        newOwner = new Owner("Pepe", "pepito", "0000", new ArrayList<>(), "email@email.com");
        newVet = new Veterinarian("Oriol", "dr gato", "1111", new ArrayList<>(), "oriol@email.com");
        newAdmin = new Admin("Admin", "admin", "8888", new ArrayList<>());
        userRepository.save(newAdmin);
        userRepository.save(newOwner);
        userRepository.save(newVet);

        LocalDate dateOfBirth = LocalDate.of(2010,06,01);
        Date dateOfBirthOld = Date.from(dateOfBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        newCat = new Cat("Níobe", dateOfBirthOld, true, List.of(CatDiseases.IBD), CatBreeds.MIXED, null, null);
        LocalDate dateOfBirthDog = LocalDate.of(2000, 01, 01);
        Date dateOfBirthDogOld = Date.from(dateOfBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        newDog = new Dog("Bombo", dateOfBirthDogOld, false, List.of(DogDiseases.ARTHRITIS), DogBreeds.HUSKY, null, null);
    }

    @AfterEach
    void tearDown() {
        petRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getUsers_Valid() throws Exception {
        //para que pueda deserializar un userdto sin el campo email en el test
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        userRepository.deleteAll();
        userRepository.save(newOwner);
        userRepository.save(newVet);
        userRepository.save(newAdmin);

        MvcResult mvcResult = mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, UserDTO.class);
        List<UserDTO> userDTOs = objectMapper.readValue(contentAsString, type);
        assertNotNull(userDTOs);
        assertEquals(3, userDTOs.size());
        assertTrue(userDTOs.stream().anyMatch(userDTO -> "Oriol".equals(userDTO.getName())));
        assertTrue(userDTOs.stream().anyMatch(userDTO -> "pepito".equals(userDTO.getUsername())));
    }

    @Test
    void finUserByUsername_Valid() throws Exception {
        userRepository.deleteAll();
        userRepository.save(newOwner);

        MvcResult mvcResult = mockMvc.perform(get("/api/users/username")
                        .param("username", "pepito")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        JsonNode userNode = objectMapper.readTree(contentAsString);

        assertNotNull(userNode);
        assertEquals(newOwner.getName(), userNode.get("name").asText());
        assertEquals(newOwner.getUsername(), userNode.get("username").asText());
    }

    @Test
    void findUserByUserName_UserNotFound() throws Exception {
        userRepository.deleteAll();
        mockMvc.perform(get("/api/users/username")
                        .param("username", "no existe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "pepito", authorities = {"ROLE_USER"})
    void getAllVeterinarians_Valid(){
        List<Veterinarian> veterinarians = userService.getAllVeterinarians();
        assertNotNull(veterinarians);
        assertTrue(veterinarians.stream().anyMatch(vet -> "Oriol".equals(vet.getName())));
        assertTrue(veterinarians.stream().anyMatch(vet -> "dr gato".equals(vet.getUsername())));
    }

    @Test
    void saveUser_Valid() throws Exception {
        userRepository.deleteAll();
        userRepository.save(testUser);
        String userJson = objectMapper.writeValueAsString(testUser);

        mockMvc.perform(post("/api/veterinarians")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated());

        List<User> users = userRepository.findAll();
        assertEquals("Test User", users.get(0).getName());
        assertEquals("test-user", users.get(0).getUsername());
    }

    @Test
    void saveUser_ExistingUser() throws Exception {
        userRepository.deleteAll();
        userRepository.save(testUser);
        String userJson = objectMapper.writeValueAsString(testUser);

        try {
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userJson))
                    .andExpect(status().isCreated());
        } catch (Exception e) {
            assertEquals("User already exists", e.getCause().getMessage());
        }
    }


    @Test
    void saveOwner_Valid() throws Exception {
        Owner owner = new Owner("Owner", "owner", "1234", new ArrayList<>() , "owner@email.com");
        String ownerJson = objectMapper.writeValueAsString(owner);

        mockMvc.perform(post("/api/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ownerJson))
                .andExpect(status().isCreated());

        Optional<User> savedOwner = userRepository.findByUsername("owner");
        assertNotNull(savedOwner);
        assertEquals("Owner", savedOwner.get().getName());
        assertEquals("owner", savedOwner.get().getUsername());
    }


    @Test
    void saveAdmin_Valid() throws Exception {
        Admin admin = new Admin("Admin", "new-admin", "1234", new ArrayList<>());
        String adminJson = objectMapper.writeValueAsString(admin);

        mockMvc.perform(post("/api/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(adminJson))
                .andExpect(status().isCreated());

        Optional<User> savedAdmin = userRepository.findByUsername("new-admin");
        assertNotNull(savedAdmin);
        assertEquals("Admin", savedAdmin.get().getName());
        assertEquals("new-admin", savedAdmin.get().getUsername());
    }

    @Test
    void saveVeterinarian_Valid() throws Exception {
        Veterinarian veterinarian = new Veterinarian("Vet", "vet", "1234", new ArrayList<>(), "vet@email.com");
        String vetJson = objectMapper.writeValueAsString(veterinarian);

        mockMvc.perform(post("/api/veterinarians")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(vetJson))
                .andExpect(status().isCreated());

        Optional<User> savedVet = userRepository.findByUsername("vet");
        assertNotNull(savedVet);
        assertEquals("Vet", savedVet.get().getName());
        assertEquals("vet", savedVet.get().getUsername());
    }

    @Test
    void registerOwner_Valid() throws Exception {
        userRepository.deleteAll();
        Owner newOwner = new Owner("Test Owner", "test_owner", "1234", new ArrayList<>(), "owner@mail.com");

        String ownerJson = objectMapper.writeValueAsString(newOwner);

        mockMvc.perform(post("/api/register/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ownerJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Owner registered successfully"));

        Optional<User> savedOwner = userRepository.findByUsername("test_owner");
        assertNotNull(savedOwner);
        assertEquals("Test Owner", savedOwner.get().getName());
        assertEquals("test_owner", savedOwner.get().getUsername());
    }

    @Test
    void registerOwner_AlreadyExists() throws Exception {
        Owner existingOwner = new Owner("Existing Owner", "existing_owner", "1234", new ArrayList<>(), "owner@mail.com");
        userRepository.save(existingOwner);

        String ownerJson = objectMapper.writeValueAsString(existingOwner);

        mockMvc.perform(post("/api/register/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ownerJson))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void registerOwner_Null() throws Exception {
        Owner nullOwner = null;
        String ownerJson = objectMapper.writeValueAsString(nullOwner);

        mockMvc.perform(post("/api/register/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ownerJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerVeterinarian_Valid() throws Exception {
        Veterinarian registerVet = new Veterinarian("Bea", "bea", "1111", new ArrayList<>(), "bea@email.com");
        String vetJson = objectMapper.writeValueAsString(registerVet);

        mockMvc.perform(post("/api/register/veterinarians")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(vetJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Veterinarian registered successfully"));

        Optional<User> savedVet = userRepository.findByUsername("bea");
        assertNotNull(savedVet);
        assertEquals("Bea", savedVet.get().getName());
        assertEquals("bea", savedVet.get().getUsername());
    }

    @Test
    void registerVeterinarian_AlreadyExists() throws Exception {
        userRepository.save(newVet);

        String vetJson = objectMapper.writeValueAsString(newVet);

        mockMvc.perform(post("/api/register/veterinarians")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(vetJson))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void registerVeterinarian_Null() throws Exception {
        Veterinarian nullVet = null;
        String vetJson = objectMapper.writeValueAsString(nullVet);

        mockMvc.perform(post("/api/register/veterinarians")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(vetJson))
                .andExpect(status().isBadRequest());
    }


    @Test
    void registerAdmin_Valid() throws Exception {
       userRepository.deleteAll();
       String adminJson = objectMapper.writeValueAsString(newAdmin);

        mockMvc.perform(post("/api/register/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(adminJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Admin registered successfully"));

        Optional<User> savedAdmin = userRepository.findByUsername("admin");
        assertNotNull(savedAdmin);
        assertEquals("Admin", savedAdmin.get().getName());
        assertEquals("admin", savedAdmin.get().getUsername());
    }

    @Test
    void registerAdmin_AlreadyExists() throws Exception {
        Admin existingAdmin = new Admin("Existing Admin", "existing_admin", "1234", new ArrayList<>());
        userRepository.save(existingAdmin);

        String adminJson = objectMapper.writeValueAsString(existingAdmin);

        mockMvc.perform(post("/api/register/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(adminJson))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void registerAdmin_Null() throws Exception {
        Admin nullAdmin = null;
        String adminJson = objectMapper.writeValueAsString(nullAdmin);

        mockMvc.perform(post("/api/register/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(adminJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void updateAdmin_Valid() throws Exception {
        Admin updatedAdmin = new Admin("Updated Admin", "updated-admin", "5678", new ArrayList<>());
        String adminJson = objectMapper.writeValueAsString(updatedAdmin);

        mockMvc.perform(put("/api/admins/" + newAdmin.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(adminJson))
                .andExpect(status().isNoContent());

        Optional<User> savedAdmin = userRepository.findByUsername("admin");
        assertNotNull(savedAdmin);
        assertEquals("Updated Admin", savedAdmin.get().getName());
        assertNotEquals("updated-admin", savedAdmin.get().getUsername());
        assertTrue(passwordEncoder.matches("5678", savedAdmin.get().getPassword()));
    }
    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void updateAdmin_NotFound() throws Exception {
        Admin existingAdmin = new Admin("Existing Admin", "existing_admin", "1234", new ArrayList<>());
        userRepository.save(existingAdmin);

        Admin updatedAdmin = new Admin("Updated Admin", "updated_admin", "5678", new ArrayList<>());
        String adminJson = objectMapper.writeValueAsString(updatedAdmin);

        mockMvc.perform(put("/api/admins/" + 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(adminJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "pepito", authorities = {"ROLE_USER"})
    void updateOwner_Valid() throws Exception {
        Owner updatedOwner = new Owner("Updated Owner", "updated_owner", "5678", new ArrayList<>(), "updated_owner@mail.com");
        String ownerJson = objectMapper.writeValueAsString(updatedOwner);

        mockMvc.perform(put("/api/owners/" + newOwner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ownerJson))
                .andExpect(status().isNoContent());

        Optional<User> savedOwner = userRepository.findByUsername("pepito");

        assertNotNull(savedOwner);
        assertEquals("Updated Owner", savedOwner.get().getName());
        assertNotEquals("updated_owner", savedOwner.get().getUsername());
        assertTrue(passwordEncoder.matches("5678", savedOwner.get().getPassword()));
        assertEquals("updated_owner@mail.com", ((Owner)savedOwner.get()).getEmail());
    }
    @Test
    @WithMockUser(username = "pepito", authorities = {"ROLE_USER"})
    void updateOwner_NotFound() throws Exception {
        Owner updatedOwner = new Owner("Updated Owner", "updated_owner", "5678", new ArrayList<>(), "updated_owner@mail.com");
        String ownerJson = objectMapper.writeValueAsString(updatedOwner);

        mockMvc.perform(put("/api/owners/" + 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ownerJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "dr gato", authorities = {"ROLE_VET"})
    void updateVeterinarian_Valid() throws Exception {
        Veterinarian updatedVeterinarian = new Veterinarian("Updated Vet", "updated-vet", "5678", new ArrayList<>(), "updatedveterinarian@mail.com");
        String veterinarianJson = objectMapper.writeValueAsString(updatedVeterinarian);

        mockMvc.perform(put("/api/veterinarians/" + newVet.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(veterinarianJson))
                .andExpect(status().isNoContent());

        Optional<User> savedVeterinarian = userRepository.findByUsername("dr gato");

        assertNotNull(savedVeterinarian);
        assertEquals("Updated Vet", savedVeterinarian.get().getName());
        assertNotEquals("updated-vet", savedVeterinarian.get().getUsername());
        assertTrue(passwordEncoder.matches("5678", savedVeterinarian.get().getPassword()));
        assertEquals("updatedveterinarian@mail.com", ((Veterinarian)savedVeterinarian.get()).getEmail());
    }

    @Test
    @WithMockUser(username = "dr gato", authorities = {"ROLE_VET"})
    void updateVeterinarian_NotFound() throws Exception {
        Veterinarian updatedVeterinarian = new Veterinarian("Updated Vet", "updated-vet", "5678", new ArrayList<>(), "updatedveterinarian@mail.com");
        String veterinarianJson = objectMapper.writeValueAsString(updatedVeterinarian);

        mockMvc.perform(put("/api/veterinarians/" + 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(veterinarianJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "pepito", authorities = {"ROLE_USER"})
    void partialUpdateOwner_Valid() throws Exception {
        UserUpdateDTO updatedOwner = new UserUpdateDTO();
        updatedOwner.setName("Updated Owner");
        updatedOwner.setPassword("5678");
        updatedOwner.setEmail("updatedowner@mail.com");

        String ownerJson = objectMapper.writeValueAsString(updatedOwner);

        mockMvc.perform(patch("/api/owners/" + newOwner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ownerJson))
                .andExpect(status().isNoContent());

        Optional<User> savedOwner = userRepository.findByUsername("pepito");

        assertNotNull(savedOwner);
        assertEquals("Updated Owner", savedOwner.get().getName());
        assertTrue(passwordEncoder.matches("5678", savedOwner.get().getPassword()));
        assertEquals("updatedowner@mail.com", ((Owner)savedOwner.get()).getEmail());
    }

    @Test
    @WithMockUser(username = "pepito", authorities = {"ROLE_USER"})
    void partialUpdateOwner_NotFound() throws Exception {
        UserUpdateDTO updatedOwner = new UserUpdateDTO();
        updatedOwner.setName("Updated Owner");
        updatedOwner.setPassword("5678");
        updatedOwner.setEmail("updated_owner@mail.com");

        String ownerJson = objectMapper.writeValueAsString(updatedOwner);

        mockMvc.perform(patch("/api/owners/" + 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ownerJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "dr gato", authorities = {"ROLE_VET"})
    void partialUpdateVeterinarian_Valid() throws Exception {
        UserUpdateDTO updatedVeterinarian = new UserUpdateDTO();
        updatedVeterinarian.setName("Updated Vet");
        updatedVeterinarian.setPassword("5678");
        updatedVeterinarian.setEmail("updatedveterinarian@mail.com");

        String veterinarianJson = objectMapper.writeValueAsString(updatedVeterinarian);

        mockMvc.perform(patch("/api/veterinarians/" + newVet.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(veterinarianJson))
                .andExpect(status().isNoContent());

        Optional<User> savedVeterinarian = userRepository.findByUsername("dr gato");

        assertNotNull(savedVeterinarian);
        assertEquals("Updated Vet", savedVeterinarian.get().getName());
        assertTrue(passwordEncoder.matches("5678", savedVeterinarian.get().getPassword()));
        assertEquals("updatedveterinarian@mail.com", ((Veterinarian)savedVeterinarian.get()).getEmail());
    }

    @Test
    @WithMockUser(username = "dr gato", authorities = {"ROLE_VET"})
    void partialUpdateVeterinarian_NotFound() throws Exception {
        UserUpdateDTO updatedVeterinarian = new UserUpdateDTO();
        updatedVeterinarian.setName("Updated Vet");
        updatedVeterinarian.setPassword("5678");
        updatedVeterinarian.setEmail("updatedveterinarian@mail.com");

        String veterinarianJson = objectMapper.writeValueAsString(updatedVeterinarian);

        mockMvc.perform(patch("/api/veterinarians/" + 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(veterinarianJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void partialUpdateAdmin_Valid() throws Exception {
        UserUpdateDTO updatedAdmin = new UserUpdateDTO();
        updatedAdmin.setName("Updated Admin");
        updatedAdmin.setPassword("5678");

        String adminJson = objectMapper.writeValueAsString(updatedAdmin);

        mockMvc.perform(patch("/api/admins/" + newAdmin.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(adminJson))
                .andExpect(status().isNoContent());

        Optional<User> savedAdmin = userRepository.findByUsername("admin");

        assertNotNull(savedAdmin);
        assertEquals("Updated Admin", savedAdmin.get().getName());
        assertTrue(passwordEncoder.matches("5678", savedAdmin.get().getPassword()));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void partialUpdateAdmin_NotFound() throws Exception {
        UserUpdateDTO updatedAdmin = new UserUpdateDTO();
        updatedAdmin.setName("Updated Admin");
        updatedAdmin.setPassword("5678");

        String adminJson = objectMapper.writeValueAsString(updatedAdmin);

        mockMvc.perform(patch("/api/admins/" + 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(adminJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "pepito", authorities = {"ROLE_USER"})
    void deleteOwnerById_Valid() throws Exception {
        mockMvc.perform(delete("/api/owners/" + newOwner.getId()))
                .andExpect(status().isOk());

        Optional<User> deletedOwner = userRepository.findById(newOwner.getId());
        assertTrue(deletedOwner.isEmpty());
    }

    @Test
    @WithMockUser(username = "pepito", authorities = {"ROLE_USER"})
    void deleteOwnerById_NotFound() throws Exception {
        mockMvc.perform(delete("/api/owners/" + 9999))
                .andExpect(status().isNotFound());
    }



    @Test
    @WithMockUser(username = "dr gato", authorities = {"ROLE_VET"})
    void deleteVeterinarianById_Valid() throws Exception {
        mockMvc.perform(delete("/api/veterinarians/" + newVet.getId()))
                .andExpect(status().isOk());

        Optional<User> deletedVeterinarian = userRepository.findById(newVet.getId());
        assertTrue(deletedVeterinarian.isEmpty());
    }

    @Test
    @WithMockUser(username = "dr gato", authorities = {"ROLE_VET"})
    void deleteVeterinarianById_WithPet() throws Exception {
        newVet.addPet(newCat);
        newCat.setOwner(newOwner);
        petRepository.save(newCat);
        mockMvc.perform(delete("/api/veterinarians/" + newVet.getId()))
                .andExpect(status().isOk());

        Optional<User> deletedVeterinarian = userRepository.findById(newVet.getId());
        assertTrue(deletedVeterinarian.isEmpty());
        Cat fromRepoCat = (Cat) petRepository.findById(newCat.getId()).get();
        assertNull(fromRepoCat.getVeterinarian());
    }



    @Test
    @WithMockUser(username = "dr gato", authorities = {"ROLE_VET"})
    void deleteVeterinarianById_NotFound() throws Exception {
        mockMvc.perform(delete("/api/veterinarians/" + 9999))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void deleteAdminById_Valid() throws Exception {
        mockMvc.perform(delete("/api/admins/" + newAdmin.getId()))
                .andExpect(status().isOk());

        Optional<User> deletedAdmin = userRepository.findById(newAdmin.getId());
        assertTrue(deletedAdmin.isEmpty());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void deleteAdminById_NotFound() throws Exception {
        mockMvc.perform(delete("/api/admins/" + 9999))
                .andExpect(status().isNotFound());
    }
}