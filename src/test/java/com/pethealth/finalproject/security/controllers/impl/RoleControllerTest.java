package com.pethealth.finalproject.security.controllers.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pethealth.finalproject.security.dtos.RoleToUserDTO;
import com.pethealth.finalproject.security.models.Role;
import com.pethealth.finalproject.security.models.User;
import com.pethealth.finalproject.security.repositories.RoleRepository;
import com.pethealth.finalproject.security.repositories.UserRepository;
import com.pethealth.finalproject.security.services.impl.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class RoleControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private RoleController roleController;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addRoleToUser() throws Exception {
        User testUser = new User();
        testUser.setName("Test One");
        testUser.setUsername("test1");
        testUser.setPassword(("1234"));
        userRepository.save(testUser);
        RoleToUserDTO roleToUserDTO = new RoleToUserDTO();
        roleToUserDTO.setUsername("test1");
        roleToUserDTO.setRoleName("ROLE_USER");

        String body = objectMapper.writeValueAsString(roleToUserDTO);

        mockMvc.perform(post("/api/roles/addtouser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent()).andReturn();

        User updatedUser = userRepository.findById(testUser.getId()).get();
        //roles es una collection y no se puede usar get(0)
        assertTrue(updatedUser.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_USER")));
    }

    @Test
    void saveRole() throws Exception {
        Role role = new Role();
        role.setName("ROLE_TEST");

        String body = objectMapper.writeValueAsString(role);

        mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        Role savedRole = roleRepository.findByName("ROLE_TEST");

        assertNotNull(savedRole);
        assertEquals("ROLE_TEST", savedRole.getName());
    }
}