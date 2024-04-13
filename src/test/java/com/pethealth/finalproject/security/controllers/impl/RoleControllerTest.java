package com.pethealth.finalproject.security.controllers.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pethealth.finalproject.security.dtos.RoleToUserDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
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

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void saveRole() {
    }

//    @Test
//    void addRoleToUser() throws Exception {
//        RoleToUserDTO roleToUserDTO = new RoleToUserDTO();
//        roleToUserDTO.setRoleName("test1");
//        roleToUserDTO.setRoleName("ROLE_USER");
//
//        String body = objectMapper.writeValueAsString(roleToUserDTO);
//
//        mockMvc.perform(post("/roles/addtouser")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(body))
//                .andExpect(status().isNoContent()).andReturn();
//    }
}