package com.pethealth.finalproject.service;

import com.pethealth.finalproject.dtos.PetReadDTO;
import com.pethealth.finalproject.model.*;
import com.pethealth.finalproject.repository.PetRepository;
import com.pethealth.finalproject.security.dtos.OwnerDTO;
import com.pethealth.finalproject.security.models.User;
import com.pethealth.finalproject.security.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestEntityManager
class PetServiceTest {

    @Autowired
    private PetService petService;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Cat newCat;
    private Owner newOwner;
    private Veterinarian newVet;
    private Dog newDog;

    @BeforeEach
    void setUp() {
        LocalDate dateOfBirth = LocalDate.of(2010,06,01);
        LocalDate deateOfBirth2 = LocalDate.of(2000, 01, 01);
        Date dateOfBirthOld = Date.from(dateOfBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        Date dateOfBirthOld2 = Date.from(deateOfBirth2.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        newCat = new Cat("Níobe", dateOfBirthOld, true, List.of(CatDiseases.IBD), CatBreeds.MIXED, null, null);
        newDog = new Dog("Bombo", dateOfBirthOld2, false, List.of(DogDiseases.ARTHRITIS), DogBreeds.HUSKY, null, null);
        newOwner = new Owner("New Owner", "new_owner", "1234", new ArrayList<>(), "owner@mail.com");
        newVet = new Veterinarian("New Vet", "new_vet", "0000",  new ArrayList<>(), "vet@mail.com");

    }

    @AfterEach
    void tearDown() {
        petRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findPetById_Valid() {
        newOwner = userRepository.save(newOwner);
        newCat.setOwner(newOwner);
        petRepository.save(newCat);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        authorities.add(new SimpleGrantedAuthority("ROLE_VET"));
        org.springframework.security.core.userdetails.User mockUser = new  org.springframework.security.core.userdetails.User(newOwner.getUsername(), newOwner.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null, authorities));

        Pet retrievedPet = petService.findPetById(newCat.getId());
        assertNotNull(retrievedPet);
        assertEquals(newCat.getName(), retrievedPet.getName());

        SecurityContextHolder.clearContext();
    }

    @Test
    void findPetById_NotFound(){
        Optional<Pet> retrievedPet = petRepository.findById(200L);
        assertFalse(retrievedPet.isPresent());
    }

    @Test
    void mapToCatEntity_AllFieldsValid() {

        CatDTO catDTO = new CatDTO();
        catDTO.setName("Kitty");
        LocalDate cattoDate = LocalDate.of(2020, 1, 1);
        Date cattoDateOld = Date.from(cattoDate.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        catDTO.setDateOfBirth(cattoDateOld);
        catDTO.setSpayedOrNeutered(true);
        userRepository.save(newOwner);
        catDTO.setOwner(newOwner);
        catDTO.setChronicDiseases(List.of(CatDiseases.NONE));
        catDTO.setCatBreed(CatBreeds.PERSIAN);

        Cat cat = petService.mapToCatEntity(catDTO);

        assertNotNull(cat);
        assertEquals("Kitty", cat.getName());
        assertEquals(cattoDateOld, cat.getDateOfBirth());
        assertTrue(cat.isSpayedOrNeutered());
        assertEquals(newOwner, cat.getOwner());
        assertEquals(List.of(CatDiseases.NONE), cat.getChronicDiseases());
        assertEquals(CatBreeds.PERSIAN, cat.getCatBreed());
    }

    @Test
    void mapToCatEntity_OwnerNull() {
        CatDTO catDTO = new CatDTO();
        catDTO.setName("Kitty");
        LocalDate kittyDateOfBirth = LocalDate.of(2020, 1, 1);
        Date kittyDate = Date.from(kittyDateOfBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        catDTO.setDateOfBirth(kittyDate);
        catDTO.setSpayedOrNeutered(true);
        catDTO.setOwner(null);
        catDTO.setChronicDiseases(List.of(CatDiseases.NONE));
        catDTO.setCatBreed(CatBreeds.PERSIAN);

        assertThrows(IllegalArgumentException.class, () -> petService.mapToCatEntity(catDTO));
    }

    @Test
    void mapToCatEntity_OwnerNotFound(){
        CatDTO catDTO = new CatDTO();
        catDTO.setName("Kitty");
        LocalDate kittyDateOfBirth = LocalDate.of(2020, 1, 1);
        Date kittyDate = Date.from(kittyDateOfBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        catDTO.setDateOfBirth(kittyDate);
        catDTO.setSpayedOrNeutered(true);
        Owner owner = new Owner();
        owner.setId(200L);
        catDTO.setOwner(owner);
        catDTO.setChronicDiseases(List.of(CatDiseases.NONE));
        catDTO.setCatBreed(CatBreeds.PERSIAN);

        assertThrows(IllegalArgumentException.class, () -> petService.mapToCatEntity(catDTO));
    }

    @Test
    void mapToCatEntity_VeterinarianNotFound(){
        CatDTO catDTO = new CatDTO();
        catDTO.setName("Kitty");
        LocalDate kittyDateOfBirth = LocalDate.of(2020, 1, 1);
        Date kittyDate = Date.from(kittyDateOfBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        catDTO.setDateOfBirth(kittyDate);
        catDTO.setSpayedOrNeutered(true);
        userRepository.save(newOwner);
        catDTO.setOwner(newOwner);
        Veterinarian vet = new Veterinarian();
        vet.setId(200L);
        catDTO.setVeterinarian(vet);
        catDTO.setChronicDiseases(List.of(CatDiseases.NONE));
        catDTO.setCatBreed(CatBreeds.PERSIAN);

        assertThrows(IllegalArgumentException.class, () -> petService.mapToCatEntity(catDTO));
    }


    @Test
    void addNewCat() {
        userRepository.deleteAll();
        userRepository.save(newOwner);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        org.springframework.security.core.userdetails.User mockUser = new  org.springframework.security.core.userdetails.User(newOwner.getUsername(), newOwner.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null, authorities));

        CatDTO catDTO = new CatDTO();
        catDTO.setName("New Cat");
        catDTO.setCatBreed(CatBreeds.BENGAL);
        catDTO.setChronicDiseases(List.of(CatDiseases.NONE));
        catDTO.setSpayedOrNeutered(false);
        LocalDate newCatDateOfBirth = LocalDate.of(2020, 1, 1);
        Date newCatDate = Date.from(newCatDateOfBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        catDTO.setDateOfBirth(newCatDate);
        catDTO.setOwner(newOwner);
        Pet addedPet = petService.addNewPet(catDTO);

        assertNotNull(addedPet);
        assertNotNull(addedPet.getHealthRecord());
        assertEquals(catDTO.getName(), addedPet.getName());
        assertEquals(catDTO.getOwner(), newOwner);
        assertEquals(catDTO.getCatBreed(), CatBreeds.BENGAL);

        SecurityContextHolder.clearContext();
    }

    @Test
    void addExistingCat() {
        petRepository.save(newCat);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        org.springframework.security.core.userdetails.User mockUser = new  org.springframework.security.core.userdetails.User(newOwner.getUsername(), newOwner.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null, authorities));

        CatDTO catDTO = new CatDTO();
        catDTO.setCatBreed(CatBreeds.BENGAL);
        catDTO.setChronicDiseases(List.of(CatDiseases.NONE));
        catDTO.setSpayedOrNeutered(false);
        LocalDate newCatDateOfBirth = LocalDate.of(2020, 1, 1);
        Date newCatDate = Date.from(newCatDateOfBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        catDTO.setDateOfBirth(newCatDate);
        userRepository.save(newOwner);
        catDTO.setOwner(newOwner);
        catDTO.setName("Níobe");
        assertThrows(ResponseStatusException.class, () -> petService.addNewPet(catDTO));
    }

    @Test
    void addInvalidPetTypeCat() {
        userRepository.deleteAll();
        userRepository.save(newOwner);
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        org.springframework.security.core.userdetails.User mockUser = new  org.springframework.security.core.userdetails.User(newOwner.getUsername(), newOwner.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null, authorities));

        PetDTO invalidDTO = new PetDTO();
        invalidDTO.setSpayedOrNeutered(false);
        invalidDTO.setName("invalid");
        assertThrows(IllegalArgumentException.class, () -> petService.addNewPet(invalidDTO));
    }

    @Test
    void addNewDog(){
        userRepository.deleteAll();
        userRepository.save(newOwner);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        org.springframework.security.core.userdetails.User mockUser = new  org.springframework.security.core.userdetails.User(newOwner.getUsername(), newOwner.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null, authorities));

        DogDTO dogDTO = new DogDTO();
        dogDTO.setName("New Dog");
        dogDTO.setDogBreed(DogBreeds.GOLDEN_RETRIEVER);
        dogDTO.setChronicDiseases(List.of(DogDiseases.NONE));
        dogDTO.setSpayedOrNeutered(false);
        LocalDate newDogDateOfBirth = LocalDate.of(2020, 1, 1);
        Date newDogDate = Date.from(newDogDateOfBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        dogDTO.setDateOfBirth(newDogDate);
        dogDTO.setOwner(newOwner);
        Pet addedPet = petService.addNewPet(dogDTO);

        assertNotNull(addedPet);
        assertEquals(dogDTO.getName(), addedPet.getName());
        assertEquals(dogDTO.getOwner(), newOwner);
        assertEquals(dogDTO.getDogBreed(), DogBreeds.GOLDEN_RETRIEVER);
    }

    @Test
    void addExistingDog(){
        userRepository.deleteAll();
        petRepository.save(newDog);
        userRepository.save(newOwner);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        org.springframework.security.core.userdetails.User mockUser = new  org.springframework.security.core.userdetails.User(newOwner.getUsername(), newOwner.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null, authorities));

        DogDTO dogDTO = new DogDTO();
        dogDTO.setDogBreed(DogBreeds.GOLDEN_RETRIEVER);
        dogDTO.setChronicDiseases(List.of(DogDiseases.NONE));
        dogDTO.setSpayedOrNeutered(false);
        LocalDate newDogDateOfBirth = LocalDate.of(2020, 1, 1);
        Date newDogDate = Date.from(newDogDateOfBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        dogDTO.setDateOfBirth(newDogDate);
        userRepository.save(newOwner);
        dogDTO.setOwner(newOwner);
        dogDTO.setName("Bombo");
        assertThrows(ResponseStatusException.class, () -> petService.addNewPet(dogDTO));
    }

    @Test
    void invalidPetTypeDog(){
        userRepository.save(newOwner);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        org.springframework.security.core.userdetails.User mockUser = new  org.springframework.security.core.userdetails.User(newOwner.getUsername(), newOwner.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null, authorities));

        PetDTO invalidDTO = new PetDTO();
        invalidDTO.setSpayedOrNeutered(false);
        invalidDTO.setName("invalid");
        assertThrows(IllegalArgumentException.class, () -> petService.addNewPet(invalidDTO));
    }

    @Test
    void findAllPets() {
        petRepository.save(newCat);
        petRepository.save(newDog);
        userRepository.save(newOwner);

        List<Pet> pets = petRepository.findAll();
        assertFalse(pets.isEmpty());
        assertTrue(pets.contains(newCat));
        assertTrue(pets.contains(newDog));
    }

    @Test
    @Transactional
    void findAllPetsByVeterinarian() {
        Veterinarian vet = new Veterinarian("Test Vet", "test_vet", "0000",  new ArrayList<>(), "vet@mail.com");
        vet = userRepository.save(vet);
        userRepository.save(newOwner);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_VET"));
        org.springframework.security.core.userdetails.User mockUser = new  org.springframework.security.core.userdetails.User(vet.getUsername(), vet.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null, authorities));

        LocalDate cattoDateBirth = LocalDate.of(2010,06,01);
        Date cattoDate = Date.from(cattoDateBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        LocalDate kittyDateBirth = LocalDate.of(2011,06,01);
        Date kittyDate = Date.from(kittyDateBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));

        Cat cat1 = new Cat("Catto", cattoDate, true, List.of(CatDiseases.IBD), CatBreeds.MIXED, newOwner, vet);
        Cat cat2 = new Cat("Kitty", kittyDate, true, List.of(CatDiseases.IBD), CatBreeds.MIXED, newOwner, vet);

        vet.addPet(cat1);
        vet.addPet(cat2);

        cat1 = petRepository.save(cat1);
        cat2 = petRepository.save(cat2);

        vet = userRepository.save(vet);

        testEntityManager.flush();
        testEntityManager.clear();

        List<PetReadDTO> pets = petService.findAllPetsByVeterinarian();

        assertNotNull(pets);
        assertEquals(cat1.getName(), pets.get(0).getName());

        SecurityContextHolder.clearContext();
    }

    @Test
    @Transactional
    void findAllPetsByOwnerTest() {
        Owner owner = new Owner("Test Owner", "test-owner", "1234", new ArrayList<>(), "owner@mail.com");
        owner = userRepository.save(owner);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        org.springframework.security.core.userdetails.User mockUser = new  org.springframework.security.core.userdetails.User(owner.getUsername(), owner.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null, authorities));

        LocalDate cattoDateBirth = LocalDate.of(2010,06,01);
        Date cattoDate = Date.from(cattoDateBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        LocalDate doggoDateBirth = LocalDate.of(2011,06,01);
        Date doggoDate = Date.from(doggoDateBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        Cat cat = new Cat("Test Cat", cattoDate, true, List.of(CatDiseases.IBD), CatBreeds.MIXED, owner, null);
        Dog dog = new Dog("Test Dog", doggoDate, false, List.of(DogDiseases.ARTHRITIS), DogBreeds.HUSKY, owner, null);

        cat = petRepository.save(cat);
        dog = petRepository.save(dog);

        testEntityManager.flush();
        testEntityManager.clear();

        List<PetReadDTO> petReadDTOs = petService.findAllPetsByOwner();

        assertNotNull(petReadDTOs);
        assertEquals(2, petReadDTOs.size());

        List<String> petNames = petReadDTOs.stream().map(PetReadDTO::getName).collect(Collectors.toList());
        assertTrue(petNames.contains(cat.getName()));
        assertTrue(petNames.contains(dog.getName()));

        SecurityContextHolder.clearContext();
    }

    @Test
    void updatePetCat_Valid(){
        newOwner = userRepository.save(newOwner);
        newCat.setOwner(newOwner);
        petRepository.save(newCat);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        org.springframework.security.core.userdetails.User mockUser = new  org.springframework.security.core.userdetails.User(newOwner.getUsername(), newOwner.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null, authorities));

        CatDTO catDTO = new CatDTO();
        catDTO.setCatBreed(CatBreeds.BENGAL);
        catDTO.setChronicDiseases(List.of(CatDiseases.NONE));
        catDTO.setSpayedOrNeutered(false);
        LocalDate newCatDateOfBirth = LocalDate.of(2015, 3, 4);
        Date newCatDate = Date.from(newCatDateOfBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        catDTO.setDateOfBirth(newCatDate);
        userRepository.save(newOwner);
        catDTO.setOwner(newOwner);
        catDTO.setName("updated cat");
        petService.updatePet(newCat.getId(), catDTO);
        Optional<Cat> updatedCat = petRepository.findCatById(newCat.getId());

        assertEquals("updated cat", updatedCat.get().getName());
        assertEquals(CatBreeds.BENGAL, updatedCat.get().getCatBreed());

        SecurityContextHolder.clearContext();
    }

    @Test
    void updatePetDog_Valid(){
        newOwner = userRepository.save(newOwner);
        newDog.setOwner(newOwner);
        petRepository.save(newDog);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        org.springframework.security.core.userdetails.User mockUser = new  org.springframework.security.core.userdetails.User(newOwner.getUsername(), newOwner.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null, authorities));

        DogDTO dogDTO = new DogDTO();
        dogDTO.setDogBreed(DogBreeds.GOLDEN_RETRIEVER);
        dogDTO.setChronicDiseases(List.of(DogDiseases.NONE));
        dogDTO.setSpayedOrNeutered(false);
        LocalDate newDogDateOfBirth = LocalDate.of(2015, 3, 4);
        Date newDogDate = Date.from(newDogDateOfBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        dogDTO.setDateOfBirth(newDogDate);
        userRepository.save(newOwner);
        dogDTO.setOwner(newOwner);
        dogDTO.setName("updated dog");
        petService.updatePet(newDog.getId(), dogDTO);
        Optional<Dog> updatedDog = petRepository.findDogById(newDog.getId());

        assertEquals("updated dog", updatedDog.get().getName());
        assertEquals(DogBreeds.GOLDEN_RETRIEVER, updatedDog.get().getDogBreed());

        SecurityContextHolder.clearContext();
    }

    @Test
    void partialUpdate_Valid(){
        newOwner = userRepository.save(newOwner);
        newCat.setOwner(newOwner);
        petRepository.save(newCat);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        org.springframework.security.core.userdetails.User mockUser = new  org.springframework.security.core.userdetails.User(newOwner.getUsername(), newOwner.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null, authorities));

        CatDTO catDTO = new CatDTO();
        catDTO.setName("patched cat");
        catDTO.setCatBreed(CatBreeds.SIAMESE);
        catDTO.setSpayedOrNeutered(false);

        petService.partialUpdate(newCat.getId(), catDTO);

        Optional<Cat> patchedCat = petRepository.findCatById(newCat.getId());

        assertEquals("patched cat", patchedCat.get().getName());
        assertEquals(CatBreeds.SIAMESE, patchedCat.get().getCatBreed());

        SecurityContextHolder.clearContext();
    }

    @Test
    void partialUpdate_Invalid(){
        newOwner = userRepository.save(newOwner);
        newCat.setOwner(newOwner);
        petRepository.save(newCat);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        org.springframework.security.core.userdetails.User mockUser = new  org.springframework.security.core.userdetails.User(newOwner.getUsername(), newOwner.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null, authorities));


        CatDTO catDTO = new CatDTO();
        catDTO.setName("patched cat");
        catDTO.setCatBreed(CatBreeds.SIAMESE);
        catDTO.setSpayedOrNeutered(false);

        assertThrows(ResponseStatusException.class, () -> petService.partialUpdate(200L, catDTO));

        SecurityContextHolder.clearContext();
    }

    //repetir con dog

    @Test
    void patchToDog_Valid(){
        newOwner = userRepository.save(newOwner);
        newDog.setOwner(newOwner);
        petRepository.save(newDog);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        org.springframework.security.core.userdetails.User mockUser = new  org.springframework.security.core.userdetails.User(newOwner.getUsername(), newOwner.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null, authorities));


        DogDTO dogDTO = new DogDTO();
        dogDTO.setName("patched dog");
        dogDTO.setDogBreed(DogBreeds.GERMAN_SHEPHERD);
        dogDTO.setSpayedOrNeutered(false);

        petService.partialUpdate(newDog.getId(), dogDTO);

        Optional<Dog> patchedDog = petRepository.findDogById(newDog.getId());

        assertEquals("patched dog", patchedDog.get().getName());
        assertEquals(DogBreeds.GERMAN_SHEPHERD, patchedDog.get().getDogBreed());

        SecurityContextHolder.clearContext();
    }

    @Test
    void patchToCat_Valid(){
        newOwner = userRepository.save(newOwner);
        newCat.setOwner(newOwner);
        petRepository.save(newCat);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        org.springframework.security.core.userdetails.User mockUser = new  org.springframework.security.core.userdetails.User(newOwner.getUsername(), newOwner.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null, authorities));

        CatDTO catDTO = new CatDTO();
        catDTO.setName("patched cat");
        catDTO.setCatBreed(CatBreeds.SIAMESE);
        catDTO.setSpayedOrNeutered(false);

        petService.partialUpdate(newCat.getId(), catDTO);

        Optional<Cat> patchedCat = petRepository.findCatById(newCat.getId());

        assertEquals("patched cat", patchedCat.get().getName());
        assertEquals(CatBreeds.SIAMESE, patchedCat.get().getCatBreed());

        SecurityContextHolder.clearContext();
    }

    @Test
    void deletePet(){
        newOwner = userRepository.save(newOwner);
        newCat.setOwner(newOwner);
        petRepository.save(newCat);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        org.springframework.security.core.userdetails.User mockUser = new  org.springframework.security.core.userdetails.User(newOwner.getUsername(), newOwner.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null, authorities));


        petService.deletePet(newCat.getId());
        Optional<Pet> deletedPet = petRepository.findById(newCat.getId());
        assertFalse(deletedPet.isPresent());

        SecurityContextHolder.clearContext();
    }

    @Test
    void addVeterinarianToPet(){
        newOwner = userRepository.save(newOwner);
        newCat.setOwner(newOwner);
        petRepository.save(newCat);
        userRepository.save(newVet);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        org.springframework.security.core.userdetails.User mockUser = new  org.springframework.security.core.userdetails.User(newOwner.getUsername(), newOwner.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null, authorities));

        Veterinarian updatedVet = petService.addVeterinarianToPet(newCat.getId(), newVet.getId());
//        Pet updatedPet = petRepository.findById(newCat.getId()).orElse(null);
        Pet updatedPet = petService.getPetWithInitializedVeterinarian(newCat.getId());
        assertNotNull(updatedVet);

        assertFalse(updatedVet.getTreatedPets().isEmpty());
        assertTrue(updatedVet.getTreatedPets().contains(updatedPet));
        assertEquals(updatedVet, updatedPet.getVeterinarian()); // solo funciona con el helper

        SecurityContextHolder.clearContext();
    }

    @Test
    void removeVeterinarianFromPet(){
        newOwner = userRepository.save(newOwner);
        newCat.setOwner(newOwner);
        petRepository.save(newCat);
        newCat.setVeterinarian(newVet);
        userRepository.save(newVet);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        org.springframework.security.core.userdetails.User mockUser = new  org.springframework.security.core.userdetails.User(newOwner.getUsername(), newOwner.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null, authorities));


        Pet updatedPet = petService.removeVeterinarianFromPet(newCat.getId(), newVet.getId());
        assertNotNull(updatedPet);
        assertNull(updatedPet.getVeterinarian());
        assertNull(newVet.getTreatedPets());

        SecurityContextHolder.clearContext();
    }
}