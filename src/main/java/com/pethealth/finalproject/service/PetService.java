package com.pethealth.finalproject.service;

import com.pethealth.finalproject.dtos.CatReadDTO;
import com.pethealth.finalproject.dtos.DogReadDTO;
import com.pethealth.finalproject.dtos.PetReadDTO;
import com.pethealth.finalproject.model.*;
import com.pethealth.finalproject.repository.HealthRecordRepository;
import com.pethealth.finalproject.repository.PetRepository;
import com.pethealth.finalproject.security.models.User;
import com.pethealth.finalproject.security.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PetService {
    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HealthRecordRepository healthRecordRepository;

    @Autowired
    private EntityManager entityManager;


    public String getCurrentUserName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            return currentUserName;
        }
        return null;
    }


public Pet findPetById(Long id){
    String currentUserName = getCurrentUserName();
    User currentUser = userRepository.findByUsername(currentUserName)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

    Pet pet = petRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found"));

    if (!(currentUser.equals(pet.getOwner()) || currentUser.equals(pet.getVeterinarian()))) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access is denied.");
    } else if (currentUser instanceof Admin){
        return pet;
    }

    return pet;
}


    public Pet addNewPet(PetDTO petDTO) {
        String currentUserName = getCurrentUserName();
        User currentUser = userRepository.findByUsername(currentUserName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
        //si es un owner se asigna automáticamente en el dto
        if (!(currentUser instanceof Owner)) {
            //entonces es un admin por lo que necesita pasar un owner en el json
            if(petDTO.getOwner() == null || petDTO.getOwner().getId() == null){
                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Owner is required to create a pet when adding as admin.");
            }
        } else {
            petDTO.setOwner((Owner) currentUser);
        }

        Pet pet;

        //map the dto to the corresponding entity
        if ((petDTO instanceof CatDTO)) {
            pet = mapToCatEntity((CatDTO) petDTO);
        } else if ((petDTO instanceof DogDTO)){
            pet = mapToDogEntity((DogDTO) petDTO);
        } else {
            throw new IllegalArgumentException("Invalid pet type.");
        }
        //check if the pet already exists, by name (añadir atributos??)
        //la ID se asigna cuando se usa el findById
        if (pet instanceof Cat) {
            Cat existingCat = (Cat) petRepository.findByName(pet.getName()).orElse(null);
            if (existingCat != null) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Cat already exists.");
            }
        } else if (pet instanceof Dog) {
            Dog existingDog = (Dog) petRepository.findByName(pet.getName()).orElse(null);
            if (existingDog != null) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Dog already exists.");
            }
        } else {
            throw new IllegalArgumentException("Invalid pet type.");
        }
        pet = petRepository.save(pet);
        //initialize a healthRecord
        HealthRecord healthRecord = new HealthRecord();
        pet.setHealthRecord(healthRecord);
        healthRecord.setPet(pet);
        healthRecordRepository.save(healthRecord);

        return pet;
    }


    Cat mapToCatEntity(CatDTO catDTO) {
        Cat cat = new Cat();
        cat.setName(catDTO.getName());
        cat.setDateOfBirth(catDTO.getDateOfBirth());
        cat.setSpayedOrNeutered(catDTO.isSpayedOrNeutered());
        //owner can't be null, it's the logged-in user (owner)
        if (catDTO.getOwner() == null) {
            throw new IllegalArgumentException("Owner is required to create a pet.");
        } else {
            User owner = userRepository.findById(catDTO.getOwner().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Owner not found"));
            cat.setOwner((Owner) owner);
        }
        //veterinarian can be null
        if(catDTO.getVeterinarian() != null){
            User veterinarian = userRepository.findById(catDTO.getVeterinarian().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Veterinarian not found"));
            cat.setVeterinarian((Veterinarian) veterinarian);
        }

        cat.setChronicDiseases(catDTO.getChronicDiseases());
        cat.setCatBreed(catDTO.getCatBreed());
        return cat;
    }

    private Dog mapToDogEntity(DogDTO dogDTO) {
        Dog dog = new Dog();
        dog.setName(dogDTO.getName());
        dog.setDateOfBirth(dogDTO.getDateOfBirth());
        dog.setSpayedOrNeutered(dogDTO.isSpayedOrNeutered());

        if (dogDTO.getOwner() == null) {
            throw new IllegalArgumentException("Owner is required to create a pet.");
        } else {
            Owner owner = (Owner) userRepository.findById(dogDTO.getOwner().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Owner not found"));
            dog.setOwner(owner);
        }
        if(dogDTO.getVeterinarian() != null) {
            Veterinarian veterinarian = (Veterinarian) userRepository.findById(dogDTO.getVeterinarian().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Veterinarian not found"));
            dog.setVeterinarian(veterinarian);
        }

        dog.setChronicDiseases(dogDTO.getChronicDiseases());
        dog.setDogBreed(dogDTO.getDogBreed());
        return dog;
    }

//método con endpoint aparte para admins:
    public List<PetReadDTO> findAllPets() {

        List<Pet> pets = petRepository.findAll();

        if(pets.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No pets found.");
        }

        List<PetReadDTO> petReadDTOs = pets.stream()
                .map(this::mapToPetReadDTO)
                .collect(Collectors.toList());

        return petReadDTOs;
    }

    public PetReadDTO mapToPetReadDTO(Pet pet) {
        PetReadDTO petReadDTO;
        if (pet instanceof Cat) {
            petReadDTO = new CatReadDTO();
            ((CatReadDTO) petReadDTO).setChronicDiseases(((Cat) pet).getChronicDiseases());
            ((CatReadDTO) petReadDTO).setCatBreed(((Cat) pet).getCatBreed());
        } else if (pet instanceof Dog) {
            petReadDTO = new DogReadDTO();
            ((DogReadDTO) petReadDTO).setChronicDiseases(((Dog) pet).getChronicDiseases());
            ((DogReadDTO) petReadDTO).setDogBreed(((Dog) pet).getDogBreed());
        } else {
            throw new IllegalArgumentException("Invalid pet type.");
        }

        petReadDTO.setName(pet.getName());
        petReadDTO.setDateOfBirth(pet.getDateOfBirth());
        petReadDTO.setSpayedOrNeutered(pet.isSpayedOrNeutered());
        petReadDTO.setOwnerId(pet.getOwner().getId());
        petReadDTO.setOwnerName(pet.getOwner().getName());

        if(!(pet.getVeterinarian() == null)) {
            petReadDTO.setVeterinarianId(pet.getVeterinarian().getId());
            petReadDTO.setVeterinarianName(pet.getVeterinarian().getName());
        }

        return petReadDTO;
    }


    @Transactional
    public List<PetReadDTO> findAllPetsByVeterinarian() {
        String currentUsername = getCurrentUserName();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
        if(!(user instanceof Veterinarian)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a veterinarian.");
        }
        //para ver si asi carga todos los pet para que pase el test
        Veterinarian veterinarian = (Veterinarian) userRepository.findByIdAndFetchPetsEagerly(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veterinarian not found."));
        Hibernate.initialize(veterinarian.getTreatedPets());

        List<Pet> pets = new ArrayList<>(veterinarian.getTreatedPets());

        if (pets.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No pets found.");
        }

        List<PetReadDTO> petReadDTOs = pets.stream()
                .map(this::mapToPetReadDTO)
                .collect(Collectors.toList());
        return petReadDTOs;
    }

    @Transactional
    public List<PetReadDTO> findAllPetsByOwner() {
        String currentUsername = getCurrentUserName();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        if(!(user instanceof Owner)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not an owner.");
        }
        Owner owner = (Owner) user;
        List<Pet> pets = new ArrayList<>(owner.getOwnedPets());
        System.out.println("number of pets: " + pets.size());
        if (pets.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No pets found.");
        }

        List<PetReadDTO> petReadDTOs = pets.stream()
                .map(this::mapToPetReadDTO)
                .collect(Collectors.toList());
        return petReadDTOs;
    }

    public void updatePet(Long id, PetDTO petDTO) {
        String currentUserName = getCurrentUserName();
        User currentUser = userRepository.findByUsername(currentUserName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
        Pet existingPet = petRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found."));
        //comprobar si el owner es el mismo que el usuario logeado o es admin
        if(!currentUser.equals(existingPet.getOwner()) || currentUser instanceof Admin){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only the owner of the pet or admin can update pet.");
        }
        if ((existingPet instanceof Cat) && (!(petDTO instanceof CatDTO)) ||
                (existingPet instanceof Dog) && (!(petDTO instanceof DogDTO))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid pet type.");
        }
        Pet updatedPet;
        if (petDTO instanceof CatDTO) {
            updatedPet = mapToCatEntity((CatDTO) petDTO);
        } else if (petDTO instanceof DogDTO) {
            updatedPet = mapToDogEntity((DogDTO) petDTO);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid pet type.");
        }

        updatedPet.setId(id);
        updatedPet.setHealthRecord(existingPet.getHealthRecord());

        petRepository.save(updatedPet);
    }

    public void partialUpdate(Long id, PetDTO petDTO){
        String currentUserName = getCurrentUserName();
        User currentUser = userRepository.findByUsername(currentUserName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
        Pet existingPet = petRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found."));
        //comprobar si el owner es el mismo que el usuario logeado o es admin
        if(!currentUser.equals(existingPet.getOwner()) || currentUser instanceof Admin){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only the owner of the pet or admin can update pet.");
        }
        if ((existingPet instanceof Cat) && (!(petDTO instanceof CatDTO)) ||
                (existingPet instanceof Dog) && (!(petDTO instanceof DogDTO))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid pet type.");
        }

        Pet patchedPet;
        if (petDTO instanceof CatDTO) {
            patchedPet = patchCatEntity((CatDTO) petDTO, (Cat) existingPet);
        } else if (petDTO instanceof DogDTO) {
            patchedPet = patchDogEntity((DogDTO) petDTO, (Dog) existingPet);
        } else {
            throw new IllegalArgumentException("Invalid pet type.");
        }

        petRepository.save(patchedPet);
    }

    private Cat patchCatEntity(CatDTO catDTO, Cat existingCat) {
        if (catDTO.getName() != null) {
            existingCat.setName(catDTO.getName());
        }
        if (catDTO.getDateOfBirth() != null){
            existingCat.setDateOfBirth(catDTO.getDateOfBirth());
        }
        existingCat.setSpayedOrNeutered(catDTO.isSpayedOrNeutered());
        if (catDTO.getCatBreed() != null){
            existingCat.setCatBreed(catDTO.getCatBreed());
        }
        if (catDTO.getChronicDiseases() != null) {
            existingCat.setChronicDiseases(catDTO.getChronicDiseases());
        }
        return existingCat;
    }

    private Dog patchDogEntity(DogDTO dogDTO, Dog existingDog) {
        if (dogDTO.getName() != null) {
            existingDog.setName(dogDTO.getName());
        }
        if (dogDTO.getDateOfBirth() != null){
            existingDog.setDateOfBirth(dogDTO.getDateOfBirth());
        }
        existingDog.setSpayedOrNeutered(dogDTO.isSpayedOrNeutered());
        if (dogDTO.getDogBreed() != null){
            existingDog.setDogBreed(dogDTO.getDogBreed());
        }
        if (dogDTO.getChronicDiseases() != null) {
            existingDog.setChronicDiseases(dogDTO.getChronicDiseases());
        }

        return existingDog;
    }

    @Transactional
    public void deletePet(Long id){
        String currentUserName = getCurrentUserName();
        User currentUser = userRepository.findByUsername(currentUserName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found."));
        if(!(currentUser.equals(pet.getOwner()) || currentUser instanceof Admin)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only the owner of the pet or admin can delete pet.");
        }

            if (pet.getOwner() != null) {
                pet.getOwner().getOwnedPets().remove(pet);
            }
            if (pet.getVeterinarian() != null) {
                pet.getVeterinarian().getTreatedPets().remove(pet);
            }

        petRepository.deleteById(id);
    }

    //add and remove veterinarians from pets
    @Transactional
    public Veterinarian addVeterinarianToPet(Long petId, Long vetId) {
        String currentUserName = getCurrentUserName();
        User currentUser = userRepository.findByUsername(currentUserName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found."));

        if(!(currentUser.equals(pet.getOwner()) || currentUser instanceof Admin)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only the owner of the pet or admin can assign a veterinarian.");
        }

        Veterinarian veterinarian = (Veterinarian) userRepository.findByIdAndFetchPetsEagerly(vetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veterinarian not found."));

        pet.setVeterinarian(veterinarian);
        if(veterinarian.getTreatedPets() == null){
            veterinarian.setTreatedPets(new HashSet<>());
        }
        veterinarian.getTreatedPets().add(pet);

        userRepository.save(veterinarian);
        petRepository.save(pet);
        return userRepository.findByIdAndFetchPetsEagerly(vetId).map(user -> {
            Veterinarian vet = (Veterinarian) user;
            vet.getTreatedPets().size();
            return vet;
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veterinarian not found."));
    }

    //helper para poder sacar el pet con el veterinario asignado
    @Transactional
    public Pet getPetWithInitializedVeterinarian(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found."));
        Hibernate.initialize(pet.getVeterinarian());
        if(pet.getVeterinarian() != null) {
            Hibernate.initialize(pet.getVeterinarian().getTreatedPets());
        }
        return pet;
    }

    @Transactional
    public Pet removeVeterinarianFromPet(Long petId, Long vetId) {
        String currentUserName = getCurrentUserName();
        User currentUser = userRepository.findByUsername(currentUserName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found."));

        if(!(currentUser.equals(pet.getOwner()) || currentUser instanceof Admin)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only the owner of the pet or admin can remove a veterinarian from a pet.");
        }

        Veterinarian veterinarian = (Veterinarian) userRepository.findById(vetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veterinarian not found."));

        veterinarian.getTreatedPets().remove(pet);
        pet.setVeterinarian(null);
        return pet;
    }
}
