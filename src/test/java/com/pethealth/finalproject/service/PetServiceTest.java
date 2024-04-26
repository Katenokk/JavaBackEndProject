package com.pethealth.finalproject.service;

import com.pethealth.finalproject.model.*;
import com.pethealth.finalproject.repository.PetRepository;
import com.pethealth.finalproject.security.dtos.OwnerDTO;
import com.pethealth.finalproject.security.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PetServiceTest {

    @Autowired
    private PetService petService;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    private Cat newCat;
    private Owner newOwner;
    private Veterinarian newVet;
    private Dog newDog;

    @BeforeEach
    void setUp() {
        LocalDate dateOfBirth = LocalDate.of(2010,06,01);
        newCat = new Cat("Níobe", dateOfBirth, true, List.of(CatDiseases.IBD), CatBreeds.MIXED, null, null);
        newDog = new Dog("Bombo", LocalDate.of(2000, 01, 01), false, List.of(DogDiseases.ARTHRITIS), DogBreeds.HUSKY, null, null);
        newOwner = new Owner("New Owner", "new_owner", "1234", new ArrayList<>(), "owner@mail.com");
        newVet = new Veterinarian("New Vet", "new_vet", "0000",  new ArrayList<>(), "vet@mail.com");

    }

    @AfterEach
    void tearDown() {
        petRepository.deleteAll();
    }

    @Test
    void findPetById() {
        petRepository.save(newCat);
        Optional<Pet> retrievedPet = petRepository.findById(newCat.getId());
        assertTrue(retrievedPet.isPresent());
        assertEquals(newCat.getName(), retrievedPet.get().getName());
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
        catDTO.setDateOfBirth(LocalDate.of(2020, 1, 1));
        catDTO.setSpayedOrNeutered(true);
        userRepository.save(newOwner);
        catDTO.setOwner(newOwner);
//        catDTO.setChronicDiseases(Collections.emptyList());
        catDTO.setCatBreed(CatBreeds.PERSIAN);

        Cat cat = petService.mapToCatEntity(catDTO);

        assertNotNull(cat);
        assertEquals("Kitty", cat.getName());
        assertEquals(LocalDate.of(2020, 1, 1), cat.getDateOfBirth());
        assertTrue(cat.isSpayedOrNeutered());
        assertEquals(newOwner, cat.getOwner());
//        assertEquals(Collections.emptyList(), cat.getChronicDiseases());
        assertEquals(CatBreeds.PERSIAN, cat.getCatBreed());
    }


    @Test
    void addNewCat() {
        userRepository.save(newOwner);
        CatDTO catDTO = new CatDTO();
        catDTO.setName("New Cat");
        catDTO.setCatBreed(CatBreeds.BENGAL);
        catDTO.setChronicDiseases(List.of(CatDiseases.NONE));
        catDTO.setSpayedOrNeutered(false);
        catDTO.setDateOfBirth(LocalDate.of(2000, 1, 1));
        catDTO.setOwner(newOwner);
        Pet addedPet = petService.addNewPet(catDTO);

        assertNotNull(addedPet);
        assertEquals(catDTO.getName(), addedPet.getName());
        assertEquals(catDTO.getOwner(), newOwner);
        assertEquals(catDTO.getCatBreed(), CatBreeds.BENGAL);
    }

    @Test
    void addExistingCat() {
        petRepository.save(newCat);

        CatDTO catDTO = new CatDTO();
        catDTO.setCatBreed(CatBreeds.BENGAL);
        catDTO.setChronicDiseases(List.of(CatDiseases.NONE));
        catDTO.setSpayedOrNeutered(false);
        catDTO.setDateOfBirth(LocalDate.of(2000, 1, 1));
        userRepository.save(newOwner);
        catDTO.setOwner(newOwner);
        catDTO.setName("Níobe");
        assertThrows(ResponseStatusException.class, () -> petService.addNewPet(catDTO));
    }

    @Test
    void addInvalidPetType() {
        PetDTO invalidDTO = new PetDTO();
        invalidDTO.setSpayedOrNeutered(false);
        invalidDTO.setName("invalid");
        assertThrows(IllegalArgumentException.class, () -> petService.addNewPet(invalidDTO));
    }

    //hacer lo mismo con DogDTO

    //añadir maptoCat y mapToDog

    @Test
    void findAllPets() {
        petRepository.save(newCat);
        petRepository.save(newDog);
        List<Pet> pets = petRepository.findAll();
        assertFalse(pets.isEmpty());
        assertTrue(pets.contains(newCat));
        assertTrue(pets.contains(newDog));
    }

    @Test
    void updatePet_Valid(){
        petRepository.save(newCat);
        CatDTO catDTO = new CatDTO();
        catDTO.setCatBreed(CatBreeds.BENGAL);
        catDTO.setChronicDiseases(List.of(CatDiseases.NONE));
        catDTO.setSpayedOrNeutered(false);
        catDTO.setDateOfBirth(LocalDate.of(2015, 3, 4));
        userRepository.save(newOwner);
        catDTO.setOwner(newOwner);
        catDTO.setName("updated cat");
        petService.updatePet(newCat.getId(), catDTO);
        Optional<Cat> updatedCat = petRepository.findCatById(newCat.getId());

        assertEquals("updated cat", updatedCat.get().getName());
        assertEquals(CatBreeds.BENGAL, updatedCat.get().getCatBreed());
    }

    //repetir con dogdto

    @Test
    void partialUpdate_Valid(){
        petRepository.save(newCat);
        CatDTO catDTO = new CatDTO();
        catDTO.setName("patched cat");
        catDTO.setCatBreed(CatBreeds.SIAMESE);
        catDTO.setSpayedOrNeutered(false);

        petService.partialUpdate(newCat.getId(), catDTO);

        Optional<Cat> patchedCat = petRepository.findCatById(newCat.getId());

        assertEquals("patched cat", patchedCat.get().getName());
        assertEquals(CatBreeds.SIAMESE, patchedCat.get().getCatBreed());
    }

    //repetir con dog
    //añadir patch to cat y to dog
}