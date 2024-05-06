package com.pethealth.finalproject.security.services.impl;

import com.pethealth.finalproject.model.*;
import com.pethealth.finalproject.repository.PetRepository;
import com.pethealth.finalproject.security.dtos.UserDTO;
import com.pethealth.finalproject.security.models.CustomUserDetails;
import com.pethealth.finalproject.security.models.Role;
import com.pethealth.finalproject.security.models.User;
import com.pethealth.finalproject.security.repositories.RoleRepository;
import com.pethealth.finalproject.security.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class UserServiceTest {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetRepository petRepository;


    @Autowired
    private RoleRepository roleRepository;


    @Autowired
    private UserService userService;




    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    private Owner newOwner;

    private Veterinarian newVet;

    private Admin newAdmin;

    @BeforeEach
    void setUp() {
        testUser = new User("Test User", "test-user", "1234", new ArrayList<>());
        newOwner = new Owner("Pepe", "pepito", "0000", new ArrayList<>(), "pepe@email.com");
        newVet = new Veterinarian("Oriol", "dr gato", "1111", new ArrayList<>(), "oriol@email.com");
        newAdmin = new Admin("Admin", "admin", "8888", new ArrayList<>());
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
        assertNotEquals("1234", savedUser.getPassword());

        User fetchedUser = userRepository.findById(savedUser.getId()).orElse(null);

        assertNotNull(fetchedUser);
        assertEquals("Test User", fetchedUser.getName());
        assertEquals(savedUser.getPassword(), fetchedUser.getPassword());
    }

    @Test
    void saveOwnerTest(){
        Owner owner = new Owner();
        owner.setName("Test Owner");
        owner.setUsername("testowner");
        owner.setPassword("password123");

        Owner savedOwner = userService.saveOwner(owner);

        assertNotNull(savedOwner);
        assertEquals("Test Owner", savedOwner.getName());

        Owner fetchedOwner = (Owner) userRepository.findById(savedOwner.getId()).orElse(null);
        assertNotNull(fetchedOwner);
        assertEquals("Test Owner", fetchedOwner.getName());
        assertNotEquals("password123", fetchedOwner.getPassword());
        assertTrue(passwordEncoder.matches("password123", fetchedOwner.getPassword()));

    }

    @Test
    public void saveOwner_NullOwner_ExceptionThrown() {
        Owner owner = null;
        assertThrows(IllegalArgumentException.class, () -> {
            userService.saveOwner(owner);
        });
    }

    @Test
    void saveVeterinarianTest(){
        Veterinarian vet = new Veterinarian();
        vet.setName("Test Vet");
        vet.setUsername("testvet");
        vet.setPassword("password123");

        Veterinarian savedVet = userService.saveVeterinarian(vet);

        assertNotNull(savedVet);
        assertEquals("Test Vet", savedVet.getName());

        Veterinarian fetchedVet = (Veterinarian) userRepository.findById(savedVet.getId()).orElse(null);
        assertNotNull(fetchedVet);
        assertEquals("Test Vet", fetchedVet.getName());
        assertNotEquals("password123", fetchedVet.getPassword());
        assertTrue(passwordEncoder.matches("password123", fetchedVet.getPassword()));
    }

    @Test
    void saveAdminTest(){
        Admin admin = new Admin();
        admin.setName("Test Admin");
        admin.setUsername("testadmin");
        admin.setPassword("password123");

        Admin savedAdmin = userService.saveAdmin(admin);

        assertNotNull(savedAdmin);
        assertEquals("Test Admin", savedAdmin.getName());

        Admin fetchedAdmin = (Admin) userRepository.findById(savedAdmin.getId()).orElse(null);
        assertNotNull(fetchedAdmin);
        assertEquals("Test Admin", fetchedAdmin.getName());
        assertNotEquals("password123", fetchedAdmin.getPassword());
        assertTrue(passwordEncoder.matches("password123", fetchedAdmin.getPassword()));
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

        Optional<User> updatedUser = userRepository.findByUsername("test-user");
        assertTrue(updatedUser.isPresent());
        assertTrue(updatedUser.get().getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_TEST")));
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
        userRepository.deleteAll();
        userRepository.save(newOwner);
        userRepository.save(newVet);
        List<UserDTO> userList = userService.getUsers();

        assertNotNull(userList);
        assertEquals(2, userList.size());
        assertEquals("Pepe", userList.get(0).getName());
        assertEquals("Oriol", userList.get(1).getName());
    }

    @Test
    void updateOwnerValid(){
        userRepository.deleteAll();
        userRepository.save(newOwner);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        CustomUserDetails mockUser = new CustomUserDetails(newOwner.getUsername(), newOwner.getPassword(), authorities, newOwner.getId());
//        org.springframework.security.core.userdetails.User mockUser = new  org.springframework.security.core.userdetails.User(newOwner.getUsername(), newOwner.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null, authorities));

        Owner updatedOwner = new Owner("Cambiado", "cambiado", "0000", new ArrayList<>(), "email@nuevo.com");
        userService.updateOwner(newOwner.getId(), updatedOwner);
        Owner fetchedOwner = (Owner) userRepository.findById(updatedOwner.getId()).orElse(null);
        assertNotNull(fetchedOwner);
        assertEquals("Cambiado", fetchedOwner.getName());
        assertEquals("cambiado", fetchedOwner.getUsername());

        SecurityContextHolder.clearContext();
    }

    @Test
    public void testUpdateOwnerNotFound() {
        userRepository.deleteAll();
        userRepository.save(newOwner);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        org.springframework.security.core.userdetails.User mockUser = new  org.springframework.security.core.userdetails.User(newOwner.getUsername(), newOwner.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null, authorities));

        Owner owner = new Owner();
        owner.setId(100L);
        owner.setUsername("testuser");

        assertThrows(ResponseStatusException.class, () -> userService.updateOwner(100L, owner));

        SecurityContextHolder.clearContext();
    }

    @Test
    void updateVeterinarianValid(){
        userRepository.deleteAll();
        userRepository.save(newVet);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_VET"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        CustomUserDetails mockUser = new CustomUserDetails(newVet.getUsername(), newVet.getPassword(), authorities, newVet.getId());
//        org.springframework.security.core.userdetails.User mockUser = new  org.springframework.security.core.userdetails.User(newVet.getUsername(), newVet.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null, authorities));

        Veterinarian updatedVet = new Veterinarian("nuevo nombre", "nuevo usuario", "0000", new ArrayList<>(), "email@nuevo.com");
        userService.updateVeterinarian(newVet.getId(), updatedVet);
        Veterinarian fetchedVet = (Veterinarian) userRepository.findById(updatedVet.getId()).orElse(null);
        assertNotNull(fetchedVet);
        assertEquals("nuevo nombre", fetchedVet.getName());
        assertEquals("nuevo usuario", fetchedVet.getUsername());

        SecurityContextHolder.clearContext();
    }

    @Test
    public void testUpdateVeterinarianNotFound() {
        userRepository.deleteAll();
        userRepository.save(newVet);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_VET"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        org.springframework.security.core.userdetails.User mockUser = new  org.springframework.security.core.userdetails.User(newVet.getUsername(), newVet.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null, authorities));

        Veterinarian vet = new Veterinarian();
        vet.setId(100L);
        vet.setUsername("testuser");

        assertThrows(ResponseStatusException.class, () -> userService.updateVeterinarian(100L, vet));

        SecurityContextHolder.clearContext();
    }

    @Test
    void updateAdminValid(){
        userRepository.deleteAll();
        userRepository.save(newAdmin);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        CustomUserDetails mockUser = new CustomUserDetails(newAdmin.getUsername(), newAdmin.getPassword(), authorities, newAdmin.getId());
//        org.springframework.security.core.userdetails.User mockUser = new  org.springframework.security.core.userdetails.User(newAdmin.getUsername(), newAdmin.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null, authorities));


        Admin updatedAdmin = new Admin("Nuevo Admin", "nuevo admin", "0000", new ArrayList<>());
        userService.updateAdmin(newAdmin.getId(), updatedAdmin);
        Admin fetchedAdmin = (Admin) userRepository.findById(updatedAdmin.getId()).orElse(null);
        assertNotNull(fetchedAdmin);
        assertEquals("Nuevo Admin", fetchedAdmin.getName());
        assertEquals("nuevo admin", fetchedAdmin.getUsername());

        SecurityContextHolder.clearContext();
    }

    @Test
    public void testUpdateAdminNotFound() {
        userRepository.deleteAll();
        userRepository.save(newAdmin);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        org.springframework.security.core.userdetails.User mockUser = new  org.springframework.security.core.userdetails.User(newAdmin.getUsername(), newAdmin.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null, authorities));

        Admin admin = new Admin();
        admin.setId(100L);
        admin.setUsername("testuser");

        assertThrows(ResponseStatusException.class, () -> userService.updateAdmin(100L, admin));

        SecurityContextHolder.clearContext();
    }

    @Test
    void partialUpdateOwnerTest() {


        userRepository.deleteAll();
        userRepository.save(newOwner);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        CustomUserDetails mockUser = new CustomUserDetails(newOwner.getUsername(), newOwner.getPassword(), authorities, newOwner.getId());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null, authorities));

        Long ownerId = newOwner.getId();
        String updatedName = "Updated Owner";
        String updatedUsername = "updated-owner";
        String updatedPassword = "updated-password";
        String updatedEmail = "updated@example.com";

        userService.partialUpdateOwner(ownerId, updatedName, updatedUsername, updatedPassword, updatedEmail);

        Owner updatedOwner = (Owner) userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found after partial update."));

        assertEquals(updatedName, updatedOwner.getName());
        assertEquals(updatedUsername, updatedOwner.getUsername());
        assertEquals(updatedEmail, updatedOwner.getEmail());
        assertTrue(passwordEncoder.matches(updatedPassword, updatedOwner.getPassword()));

        SecurityContextHolder.clearContext();
    }

    @Test
    void partialUpdateVeterinarianTest() {
        userRepository.save(newVet);

        Long vetId = newVet.getId();
        String updatedName = "Updated Vet";
        String updatedUsername = "updated-vet";
        String updatedPassword = "updated-password";
        String updatedEmail = "updated@example.com";

        userService.partialUpdateVeterinarian(vetId, updatedName, updatedUsername, updatedPassword, updatedEmail);

        Veterinarian updatedVet = (Veterinarian) userRepository.findById(vetId)
                .orElseThrow(() -> new RuntimeException("Veterinarian not found after partial update."));

        assertEquals(updatedName, updatedVet.getName());
        assertEquals(updatedUsername, updatedVet.getUsername());
        assertEquals(updatedEmail, updatedVet.getEmail());
        assertTrue(passwordEncoder.matches(updatedPassword, updatedVet.getPassword()));
    }

    @Test
    void partialUpdateAdminTest() {
        userRepository.save(newAdmin);

        Long vetId = newAdmin.getId();
        String updatedName = "Updated Admin";
        String updatedUsername = "updated-admin";
        String updatedPassword = "updated-password";

        userService.partialUpdateAdmin(vetId, updatedName, updatedUsername, updatedPassword);

        Admin updatedAdmin = (Admin) userRepository.findById(vetId)
                .orElseThrow(() -> new RuntimeException("Admin not found after partial update."));

        assertEquals(updatedName, updatedAdmin.getName());
        assertEquals(updatedUsername, updatedAdmin.getUsername());

        assertTrue(passwordEncoder.matches(updatedPassword, updatedAdmin.getPassword()));
    }

    //falta probar excepciones de los updates

    @Test
    void deleteOwnerTest(){
        Owner savedOwner = userRepository.save(newOwner);

        userService.deleteOwner(savedOwner.getId());

        Optional<User> deletedOwner =  userRepository.findById(savedOwner.getId());
        assertFalse(deletedOwner.isPresent());
    }

    @Test
    @Transactional
    void deleteOwnerWithPetTest(){
        Owner savedOwner = userRepository.save(newOwner);
        LocalDate dateOfBirth = LocalDate.of(2010,06,01);
        Date dateOfBirthOld = Date.from(dateOfBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        Cat newCat = new Cat("Níobe", dateOfBirthOld, true, List.of(CatDiseases.IBD), CatBreeds.MIXED, null, null);

        savedOwner.addPet(newCat);
        petRepository.save(newCat);

        userService.deleteOwner(savedOwner.getId());

        Optional<User> deletedOwner =  userRepository.findById(savedOwner.getId());
        assertFalse(deletedOwner.isPresent());
        Optional<Cat> existingCat = petRepository.findCatById(newCat.getId());
        assertFalse(existingCat.isPresent());
    }

    @Test
    void deleteVeterinarianWithPetTest(){
        Veterinarian savedVet = userRepository.save(newVet);
        LocalDate dateOfBirth = LocalDate.of(2010,06,01);
        Date dateOfBirthOld = Date.from(dateOfBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        Cat newCat = new Cat("Níobe", dateOfBirthOld, true, List.of(CatDiseases.IBD), CatBreeds.MIXED, null, null);

        savedVet.addPet(newCat);
        petRepository.save(newCat);
        userService.deleteVeterinarian(savedVet.getId());

        Optional<User> deletedVet =  userRepository.findById(savedVet.getId());
        assertFalse(deletedVet.isPresent());
        Optional<Cat> existingCat = petRepository.findCatById(newCat.getId());
        assertTrue(existingCat.isPresent());
    }

    @Test
    void deleteVeterinarianWithNoPetTest(){
        Veterinarian savedVet = userRepository.save(newVet);

        userService.deleteVeterinarian(savedVet.getId());

        Optional<User> deletedVet =  userRepository.findById(savedVet.getId());
        assertFalse(deletedVet.isPresent());
    }

    @Test
    void deleteAdminTest(){
        Admin savedAdmin = userRepository.save(newAdmin);

        userService.deleteAdmin(savedAdmin.getId());

        Optional<User> deletedAdmin =  userRepository.findById(savedAdmin.getId());
        assertFalse(deletedAdmin.isPresent());
    }



}