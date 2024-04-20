package com.pethealth.finalproject.security.services.impl;

import com.pethealth.finalproject.security.models.Role;
import com.pethealth.finalproject.security.models.User;
import com.pethealth.finalproject.security.repositories.RoleRepository;
import com.pethealth.finalproject.security.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("Test User", "test-user", "1234", new ArrayList<>());
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void saveUser() {
        User savedUser = userService.saveUser(testUser);

        assertNotNull(savedUser);
        assertEquals("Test User", savedUser.getName());
        assertNotEquals("1234", savedUser.getPassword()); // Password should be encoded

        User fetchedUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertNotNull(fetchedUser);
        assertEquals("Test User", fetchedUser.getName());
        assertEquals(savedUser.getPassword(), fetchedUser.getPassword());
    }

    @Test
    void saveRole() {
        Role role = new Role();
        role.setName("TEST_ROLE");
        Role savedRole = roleRepository.save(role);

        assertNotNull(savedRole);
        assertEquals("TEST_ROLE", savedRole.getName());
        Role fetchedRole = roleRepository.findById(savedRole.getId()).orElse(null);
        assertNotNull(fetchedRole);
        assertEquals("TEST_ROLE", fetchedRole.getName());
    }

    @Test
    void addRoleToUser() {
        userRepository.save(testUser);

        Role role = new Role();
        role.setName("ROLE_TEST");
        roleRepository.save(role);

        userService.addRoleToUser("test-user", "ROLE_TEST");

        User updatedUser = userRepository.findByUsername("test-user");
        assertNotNull(updatedUser);
        assertTrue(updatedUser.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_TEST")));
    }

    @Test
    void getUser() {
        userRepository.save(testUser);
        User fetchedUser = userService.getUser("test-user");
        assertNotNull(fetchedUser);
        assertEquals("test-user", fetchedUser.getUsername());
    }

    @Test
    void getUsers() {
        User user1 = new User();
        user1.setUsername("user1");
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("user2");
        userRepository.save(user2);
        List<User> userList = userService.getUsers();

        assertNotNull(userList);
        assertEquals(2, userList.size());
        assertEquals("user1", userList.get(0).getUsername());
        assertEquals("user2", userList.get(1).getUsername());
    }

    @Test
    void updateUserValid(){
        User savedUser = userRepository.save(testUser);
        User updatedUser = new User();
        updatedUser.setId(savedUser.getId());
        updatedUser.setUsername("updateduser");

        userService.updateUser(savedUser.getId(), updatedUser);

        User fetchedUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertNotNull(fetchedUser);
        assertEquals("updateduser", fetchedUser.getUsername());
    }

    @Test
    public void testUpdateUserNotFound() {
        User user = new User();
        user.setId(100L);
        user.setUsername("testuser");

        assertThrows(ResponseStatusException.class, () -> userService.updateUser(100L, user));
    }
}