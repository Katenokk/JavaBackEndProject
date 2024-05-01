package com.pethealth.finalproject.service;

import com.pethealth.finalproject.model.*;
import com.pethealth.finalproject.repository.PetRepository;
import com.pethealth.finalproject.security.dtos.OwnerDTO;
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
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        catDTO.setChronicDiseases(List.of(CatDiseases.NONE));
        catDTO.setCatBreed(CatBreeds.PERSIAN);

        Cat cat = petService.mapToCatEntity(catDTO);

        assertNotNull(cat);
        assertEquals("Kitty", cat.getName());
        assertEquals(LocalDate.of(2020, 1, 1), cat.getDateOfBirth());
        assertTrue(cat.isSpayedOrNeutered());
        assertEquals(newOwner, cat.getOwner());
        assertEquals(List.of(CatDiseases.NONE), cat.getChronicDiseases());
        assertEquals(CatBreeds.PERSIAN, cat.getCatBreed());
    }

    @Test
    void mapToCatEntity_OwnerNull() {
        CatDTO catDTO = new CatDTO();
        catDTO.setName("Kitty");
        catDTO.setDateOfBirth(LocalDate.of(2020, 1, 1));
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
        catDTO.setDateOfBirth(LocalDate.of(2020, 1, 1));
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
        catDTO.setDateOfBirth(LocalDate.of(2020, 1, 1));
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

    //no repetir con maptodogentity?


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
        assertNotNull(addedPet.getHealthRecord());
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
    void addInvalidPetTypeCat() {
        PetDTO invalidDTO = new PetDTO();
        invalidDTO.setSpayedOrNeutered(false);
        invalidDTO.setName("invalid");
        assertThrows(IllegalArgumentException.class, () -> petService.addNewPet(invalidDTO));
    }

    @Test
    void addNewDog(){
        userRepository.save(newOwner);
        DogDTO dogDTO = new DogDTO();
        dogDTO.setName("New Dog");
        dogDTO.setDogBreed(DogBreeds.GOLDEN_RETRIEVER);
        dogDTO.setChronicDiseases(List.of(DogDiseases.NONE));
        dogDTO.setSpayedOrNeutered(false);
        dogDTO.setDateOfBirth(LocalDate.of(2000, 1, 1));
        dogDTO.setOwner(newOwner);
        Pet addedPet = petService.addNewPet(dogDTO);

        assertNotNull(addedPet);
        assertEquals(dogDTO.getName(), addedPet.getName());
        assertEquals(dogDTO.getOwner(), newOwner);
        assertEquals(dogDTO.getDogBreed(), DogBreeds.GOLDEN_RETRIEVER);
    }

    @Test
    void addExistingDog(){
        petRepository.save(newDog);

        DogDTO dogDTO = new DogDTO();
        dogDTO.setDogBreed(DogBreeds.GOLDEN_RETRIEVER);
        dogDTO.setChronicDiseases(List.of(DogDiseases.NONE));
        dogDTO.setSpayedOrNeutered(false);
        dogDTO.setDateOfBirth(LocalDate.of(2000, 1, 1));
        userRepository.save(newOwner);
        dogDTO.setOwner(newOwner);
        dogDTO.setName("Bombo");
        assertThrows(ResponseStatusException.class, () -> petService.addNewPet(dogDTO));
    }

    @Test
    void invalidPetTypeDog(){
        PetDTO invalidDTO = new PetDTO();
        invalidDTO.setSpayedOrNeutered(false);
        invalidDTO.setName("invalid");
        assertThrows(IllegalArgumentException.class, () -> petService.addNewPet(invalidDTO));
    }

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
    void updatePetCat_Valid(){
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

    @Test
    void updatePetDog_Valid(){
        petRepository.save(newDog);
        DogDTO dogDTO = new DogDTO();
        dogDTO.setDogBreed(DogBreeds.GOLDEN_RETRIEVER);
        dogDTO.setChronicDiseases(List.of(DogDiseases.NONE));
        dogDTO.setSpayedOrNeutered(false);
        dogDTO.setDateOfBirth(LocalDate.of(2015, 3, 4));
        userRepository.save(newOwner);
        dogDTO.setOwner(newOwner);
        dogDTO.setName("updated dog");
        petService.updatePet(newDog.getId(), dogDTO);
        Optional<Dog> updatedDog = petRepository.findDogById(newDog.getId());

        assertEquals("updated dog", updatedDog.get().getName());
        assertEquals(DogBreeds.GOLDEN_RETRIEVER, updatedDog.get().getDogBreed());
    }

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

    @Test
    void partialUpdate_Invalid(){
        petRepository.save(newCat);
        CatDTO catDTO = new CatDTO();
        catDTO.setName("patched cat");
        catDTO.setCatBreed(CatBreeds.SIAMESE);
        catDTO.setSpayedOrNeutered(false);

        assertThrows(ResponseStatusException.class, () -> petService.partialUpdate(200L, catDTO));
    }

    //repetir con dog

    @Test
    void patchToDog_Valid(){
        petRepository.save(newDog);
        DogDTO dogDTO = new DogDTO();
        dogDTO.setName("patched dog");
        dogDTO.setDogBreed(DogBreeds.GERMAN_SHEPHERD);
        dogDTO.setSpayedOrNeutered(false);

        petService.partialUpdate(newDog.getId(), dogDTO);

        Optional<Dog> patchedDog = petRepository.findDogById(newDog.getId());

        assertEquals("patched dog", patchedDog.get().getName());
        assertEquals(DogBreeds.GERMAN_SHEPHERD, patchedDog.get().getDogBreed());
    }

    @Test
    void patchToCat_Valid(){
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

    @Test
    void deletePet(){
        petRepository.save(newCat);
        petService.deletePet(newCat.getId());
        Optional<Pet> deletedPet = petRepository.findById(newCat.getId());
        assertFalse(deletedPet.isPresent());
    }

    @Test
    void addVeterinarianToPet(){

        petRepository.save(newCat);
        userRepository.save(newVet);
        Veterinarian updatedVet = petService.addVeterinarianToPet(newCat.getId(), newVet.getId());
//        Pet updatedPet = petRepository.findById(newCat.getId()).orElse(null);
        Pet updatedPet = petService.getPetWithInitializedVeterinarian(newCat.getId());
        assertNotNull(updatedVet);

        assertFalse(updatedVet.getTreatedPets().isEmpty());
        assertTrue(updatedVet.getTreatedPets().contains(updatedPet));
        assertEquals(updatedVet, updatedPet.getVeterinarian()); // solo funciona con el helper
    }

    @Test
    void removeVeterinarianFromPet(){
        petRepository.save(newCat);
        userRepository.save(newVet);
        newCat.setVeterinarian(newVet);
        petRepository.save(newCat);

        Pet updatedPet = petService.removeVeterinarianFromPet(newCat.getId(), newVet.getId());
        assertNotNull(updatedPet);
        assertNull(updatedPet.getVeterinarian());
        assertFalse(newVet.getTreatedPets().contains(newCat));
    }
}