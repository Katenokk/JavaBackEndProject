package com.pethealth.finalproject.security.controllers.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pethealth.finalproject.model.*;
import com.pethealth.finalproject.repository.PetRepository;
import com.pethealth.finalproject.security.dtos.UserDTO;
import com.pethealth.finalproject.security.dtos.UserUpdateDTO;
import com.pethealth.finalproject.security.models.User;
import com.pethealth.finalproject.security.repositories.UserRepository;
import com.pethealth.finalproject.security.services.impl.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
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
//        objectMapper.registerModule(new JavaTimeModule());
        //para que pueda deserializar un userdto sin el campo email en el test
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        testUser = new User("Test User", "test-user", "1234", new ArrayList<>());
        newOwner = new Owner("Pepe", "pepito", "0000", new ArrayList<>(), "email.com");
        newVet = new Veterinarian("Oriol", "dr gato", "1111", new ArrayList<>(), "oriol@email.com");
        newAdmin = new Admin("Admin", "admin", "888", new ArrayList<>());
//        LocalDate dateOfBirth = LocalDate.of(2010,06,01);
//        newCat = new Cat("Níobe", dateOfBirth, true, List.of(CatDiseases.IBD), CatBreeds.MIXED, null, null);
//        newDog = new Dog("Bombo", LocalDate.of(2000, 01, 01), false, List.of(DogDiseases.ARTHRITIS), DogBreeds.HUSKY, null, null);
//        newOwner = new Owner("New Owner", "new_owner", "1234", new ArrayList<>(), "owner@mail.com");
//        userRepository.save(newOwner);
//        newVet = new Veterinarian("New Vet", "new_vet", "0000",  new ArrayList<>(), "vet@mail.com");
//        userRepository.save(newVet);
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
    void saveUser_Valid() throws Exception {
        userRepository.deleteAll();
        userRepository.save(testUser);
        String userJson = objectMapper.writeValueAsString(testUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated());

        List<User> users = userRepository.findAll();
        assertEquals(1, users.size());
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

    //falta el de 422 x 3

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
    void updateAdmin_Valid() throws Exception {
        Admin existingAdmin = new Admin("Existing Admin", "existing_admin", "1234", new ArrayList<>());
        userRepository.save(existingAdmin);

        Admin updatedAdmin = new Admin("Updated Admin", "updated_admin", "5678", new ArrayList<>());
        String adminJson = objectMapper.writeValueAsString(updatedAdmin);

        mockMvc.perform(put("/api/admins/" + existingAdmin.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(adminJson))
                .andExpect(status().isNoContent());

        Optional<User> savedAdmin = userRepository.findByUsername("updated_admin");
        assertNotNull(savedAdmin);
        assertEquals("Updated Admin", savedAdmin.get().getName());
        assertEquals("updated_admin", savedAdmin.get().getUsername());
        assertTrue(passwordEncoder.matches("5678", savedAdmin.get().getPassword()));
    }
    @Test
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
    void updateOwner_Valid() throws Exception {
        Owner existingOwner = new Owner("Existing Owner", "existing_owner", "1234", new ArrayList<>(), "owner@mail.com");
        userRepository.save(existingOwner);

        Owner updatedOwner = new Owner("Updated Owner", "updated_owner", "5678", new ArrayList<>(), "updated_owner@mail.com");
        String ownerJson = objectMapper.writeValueAsString(updatedOwner);

        mockMvc.perform(put("/api/owners/" + existingOwner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ownerJson))
                .andExpect(status().isNoContent());

        Optional<User> savedOwner = userRepository.findByUsername("updated_owner");

        assertNotNull(savedOwner);
        assertEquals("Updated Owner", savedOwner.get().getName());
        assertEquals("updated_owner", savedOwner.get().getUsername());
        assertTrue(passwordEncoder.matches("5678", savedOwner.get().getPassword()));
        assertEquals("updated_owner@mail.com", ((Owner)savedOwner.get()).getEmail());
    }
    @Test
    void updateOwner_NotFound() throws Exception {
        Owner existingOwner = new Owner("Existing Owner", "existing_owner", "1234", new ArrayList<>(), "owner@mail.com");
        userRepository.save(existingOwner);

        Owner updatedOwner = new Owner("Updated Owner", "updated_owner", "5678", new ArrayList<>(), "updated_owner@mail.com");
        String ownerJson = objectMapper.writeValueAsString(updatedOwner);

        mockMvc.perform(put("/api/owners/" + 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ownerJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateVeterinarian_Valid() throws Exception {
        Veterinarian existingVeterinarian = new Veterinarian("Existing Veterinarian", "existing_veterinarian", "1234", new ArrayList<>(), "veterinarian@mail.com");
        userRepository.save(existingVeterinarian);

        Veterinarian updatedVeterinarian = new Veterinarian("Updated Veterinarian", "updated_veterinarian", "5678", new ArrayList<>(), "updated_veterinarian@mail.com");
        String veterinarianJson = objectMapper.writeValueAsString(updatedVeterinarian);

        mockMvc.perform(put("/api/veterinarians/" + existingVeterinarian.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(veterinarianJson))
                .andExpect(status().isNoContent());

        Optional<User> savedVeterinarian = userRepository.findByUsername("updated_veterinarian");

        assertNotNull(savedVeterinarian);
        assertEquals("Updated Veterinarian", savedVeterinarian.get().getName());
        assertEquals("updated_veterinarian", savedVeterinarian.get().getUsername());
        assertTrue(passwordEncoder.matches("5678", savedVeterinarian.get().getPassword()));
        assertEquals("updated_veterinarian@mail.com", ((Veterinarian)savedVeterinarian.get()).getEmail());
    }

    @Test
    void updateVeterinarian_NotFound() throws Exception {
        Veterinarian existingVeterinarian = new Veterinarian("Existing Veterinarian", "existing_veterinarian", passwordEncoder.encode("1234"), new ArrayList<>(), "veterinarian@mail.com");
        userRepository.save(existingVeterinarian);

        Veterinarian updatedVeterinarian = new Veterinarian("Updated Veterinarian", "updated_veterinarian", "5678", new ArrayList<>(), "updated_veterinarian@mail.com");
        String veterinarianJson = objectMapper.writeValueAsString(updatedVeterinarian);

        mockMvc.perform(put("/api/veterinarians/" + 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(veterinarianJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void partialUpdateOwner_Valid() throws Exception {
        Owner existingOwner = new Owner("Existing Owner", "existing_owner", "1234", new ArrayList<>(), "owner@mail.com");
        userRepository.save(existingOwner);

        UserUpdateDTO updatedOwner = new UserUpdateDTO();
        updatedOwner.setName("Updated Owner");
        updatedOwner.setUsername("updated_owner");
        updatedOwner.setPassword("5678");
        updatedOwner.setEmail("updated_owner@mail.com");

        String ownerJson = objectMapper.writeValueAsString(updatedOwner);

        mockMvc.perform(patch("/api/owners/" + existingOwner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ownerJson))
                .andExpect(status().isNoContent());

        Optional<User> savedOwner = userRepository.findByUsername("updated_owner");

        assertNotNull(savedOwner);
        assertEquals("Updated Owner", savedOwner.get().getName());
        assertEquals("updated_owner", savedOwner.get().getUsername());
        assertTrue(passwordEncoder.matches("5678", savedOwner.get().getPassword()));
        assertEquals("updated_owner@mail.com", ((Owner)savedOwner.get()).getEmail());
    }

    @Test
    void partialUpdateOwner_NotFound() throws Exception {
        Owner existingOwner = new Owner("Existing Owner", "existing_owner", "1234", new ArrayList<>(), "owner@mail.com");
        userRepository.save(existingOwner);

        UserUpdateDTO updatedOwner = new UserUpdateDTO();
        updatedOwner.setName("Updated Owner");
        updatedOwner.setUsername("updated_owner");
        updatedOwner.setPassword("5678");
        updatedOwner.setEmail("updated_owner@mail.com");

        String ownerJson = objectMapper.writeValueAsString(updatedOwner);

        mockMvc.perform(patch("/api/owners/" + 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ownerJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void partialUpdateVeterinarian_Valid() throws Exception {
        Veterinarian existingVeterinarian = new Veterinarian("Existing Veterinarian", "existing_veterinarian", "1234", new ArrayList<>(), "veterinarian@mail.com");
        userRepository.save(existingVeterinarian);

        UserUpdateDTO updatedVeterinarian = new UserUpdateDTO();
        updatedVeterinarian.setName("Updated Veterinarian");
        updatedVeterinarian.setUsername("updated_veterinarian");
        updatedVeterinarian.setPassword("5678");
        updatedVeterinarian.setEmail("updated_veterinarian@mail.com");

        String veterinarianJson = objectMapper.writeValueAsString(updatedVeterinarian);

        mockMvc.perform(patch("/api/veterinarians/" + existingVeterinarian.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(veterinarianJson))
                .andExpect(status().isNoContent());

        Optional<User> savedVeterinarian = userRepository.findByUsername("updated_veterinarian");

        assertNotNull(savedVeterinarian);
        assertEquals("Updated Veterinarian", savedVeterinarian.get().getName());
        assertEquals("updated_veterinarian", savedVeterinarian.get().getUsername());
        assertTrue(passwordEncoder.matches("5678", savedVeterinarian.get().getPassword()));
        assertEquals("updated_veterinarian@mail.com", ((Veterinarian)savedVeterinarian.get()).getEmail());
    }

    @Test
    void partialUpdateVeterinarian_NotFound() throws Exception {
        Veterinarian existingVeterinarian = new Veterinarian("Existing Veterinarian", "existing_veterinarian", "1234", new ArrayList<>(), "veterinarian@mail.com");
        userRepository.save(existingVeterinarian);

        UserUpdateDTO updatedVeterinarian = new UserUpdateDTO();
        updatedVeterinarian.setName("Updated Veterinarian");
        updatedVeterinarian.setUsername("updated_veterinarian");
        updatedVeterinarian.setPassword("5678");
        updatedVeterinarian.setEmail("updated_veterinarian@mail.com");

        String veterinarianJson = objectMapper.writeValueAsString(updatedVeterinarian);

        mockMvc.perform(patch("/api/veterinarians/" + 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(veterinarianJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void partialUpdateAdmin_Valid() throws Exception {
        Admin existingAdmin = new Admin("Existing Admin", "existing_admin", "1234", new ArrayList<>());
        userRepository.save(existingAdmin);

        UserUpdateDTO updatedAdmin = new UserUpdateDTO();
        updatedAdmin.setName("Updated Admin");
        updatedAdmin.setUsername("updated_admin");
        updatedAdmin.setPassword("5678");

        String adminJson = objectMapper.writeValueAsString(updatedAdmin);

        mockMvc.perform(patch("/api/admins/" + existingAdmin.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(adminJson))
                .andExpect(status().isNoContent());

        Optional<User> savedAdmin = userRepository.findByUsername("updated_admin");

        assertNotNull(savedAdmin);
        assertEquals("Updated Admin", savedAdmin.get().getName());
        assertEquals("updated_admin", savedAdmin.get().getUsername());
        assertTrue(passwordEncoder.matches("5678", savedAdmin.get().getPassword()));
    }

    @Test
    void partialUpdateAdmin_NotFound() throws Exception {
        Admin existingAdmin = new Admin("Existing Admin", "existing_admin", "1234", new ArrayList<>());
        userRepository.save(existingAdmin);

        UserUpdateDTO updatedAdmin = new UserUpdateDTO();
        updatedAdmin.setName("Updated Admin");
        updatedAdmin.setUsername("updated_admin");
        updatedAdmin.setPassword("5678");

        String adminJson = objectMapper.writeValueAsString(updatedAdmin);

        mockMvc.perform(patch("/api/admins/" + 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(adminJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteOwnerById_Valid() throws Exception {
        userRepository.save(newOwner);

        mockMvc.perform(delete("/api/owners/" + newOwner.getId()))
                .andExpect(status().isOk());

        Optional<User> deletedOwner = userRepository.findById(newOwner.getId());
        assertTrue(deletedOwner.isEmpty());
    }

    @Test
    void deleteOwnerById_NotFound() throws Exception {
        mockMvc.perform(delete("/api/owners/" + 9999))
                .andExpect(status().isNotFound());
    }



    @Test
    void deleteVeterinarianById_Valid() throws Exception {

        Veterinarian existingVeterinarian = new Veterinarian("Existing Veterinarian", "existing_veterinarian", "1234", new ArrayList<>(), "veterinarian@mail.com");
        userRepository.save(existingVeterinarian);


        mockMvc.perform(delete("/api/veterinarians/" + existingVeterinarian.getId()))
                .andExpect(status().isOk());

        Optional<User> deletedVeterinarian = userRepository.findById(existingVeterinarian.getId());
        assertTrue(deletedVeterinarian.isEmpty());
    }

    @Test
    void deleteVeterinarianById_NotFound() throws Exception {
        mockMvc.perform(delete("/api/veterinarians/" + 9999))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAdminById_Valid() throws Exception {
        Admin existingAdmin = new Admin("Existing Admin", "existing_admin", "1234", new ArrayList<>());
        userRepository.save(existingAdmin);

        mockMvc.perform(delete("/api/admins/" + existingAdmin.getId()))
                .andExpect(status().isOk());

        Optional<User> deletedAdmin = userRepository.findById(existingAdmin.getId());
        assertTrue(deletedAdmin.isEmpty());
    }

    @Test
    void deleteAdminById_NotFound() throws Exception {
        mockMvc.perform(delete("/api/admins/" + 9999))
                .andExpect(status().isNotFound());
    }
}