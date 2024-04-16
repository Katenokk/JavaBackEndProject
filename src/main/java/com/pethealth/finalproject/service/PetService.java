package com.pethealth.finalproject.service;

import com.pethealth.finalproject.model.*;
import com.pethealth.finalproject.repository.PetRepository;
import com.pethealth.finalproject.security.models.User;
import com.pethealth.finalproject.security.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    public Pet findPetById(Long id){
        return petRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found"));
    }

//    public Pet addNewPet(Pet pet){
//        pet = petRepository.findById(pet.getId()).orElseThrow( ()-> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Pet already exists.") );
//        return petRepository.save(pet);
//    }


//    public Pet addNewPet(Pet pet) {
//        if (pet instanceof Cat || pet instanceof Dog) {
//            // Check if the pet with the given ID already exists
//            if (pet.getId() != null && petRepository.existsById(pet.getId())) {
//                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Pet already exists.");
//            }
//            return petRepository.save(pet);
//        } else {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid pet type. Only Cat and Dog are allowed.");
//        }
//    }

    public Pet addNewPet(Pet pet) {
        if (pet instanceof Cat) {
            return addNewCat((Cat) pet);
        } else if (pet instanceof Dog) {
            return addNewDog((Dog) pet);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid pet type. Only Cat and Dog are allowed.");
        }
    }

//    private Cat addNewCat(Cat cat) {
//        // Check if the cat with the given ID already exists
////        if (cat.getId() != null && petRepository.existsById(cat.getId())) {
////            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Cat already exists.");
////        }
//        cat = (Cat) petRepository.findById(cat.getId()).orElseThrow( ()-> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Pet already exists.") );
//        return petRepository.save(cat);
//    }

    private Cat addNewCat(Cat cat) {
        Cat existingCat = petRepository.findCatById(cat.getId()).orElse(null);
        if (existingCat != null) {
        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Cat already exists.");
    }
        // Retrieve the owner ID from the cat object
        Long ownerId = null;
        if (cat.getOwner() != null) {
            ownerId = cat.getOwner().getId();
        }

        Long veterinarianId = null;
        if (cat.getVeterinarian() != null) {
            veterinarianId = cat.getVeterinarian().getId();
        }

        if (ownerId != null) {
            System.out.println("this is the owner id" + ownerId);
            // Retrieve the owner from the database based on the owner ID
            User owner = userRepository.findById(ownerId).orElse(null);
            if (owner == null) {
                throw new IllegalArgumentException("Owner with ID " + ownerId + " not found.");
            }
            cat.setOwner((Owner) owner);
        }

        if (veterinarianId != null) {
            System.out.println("this is the vet id" + veterinarianId);
            // Retrieve the vet from the database based on the owner ID
            User veterinarian = userRepository.findById(veterinarianId).orElse(null);
            if (veterinarian == null) {
                throw new IllegalArgumentException("Veterinarian with ID " + veterinarianId + " not found.");
            }
            cat.setVeterinarian((Veterinarian) veterinarian);
        }
        return petRepository.save(cat);
    }

    public Pet addPetprobandoo(Pet pet) {
        Pet existingPet = petRepository.findById(pet.getId()).orElse(null);
        if (existingPet != null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "pet already exists.");
        }
        // Retrieve the owner ID from the cat object
        Long ownerId = null;
        if (pet.getOwner() != null) {
            ownerId = pet.getOwner().getId();
        }

        Long veterinarianId = null;
        if (pet.getVeterinarian() != null) {
            veterinarianId = pet.getVeterinarian().getId();
        }

        if (ownerId != null) {
            System.out.println("this is the owner id" + ownerId);
            // Retrieve the owner from the database based on the owner ID
            User owner = userRepository.findById(ownerId).orElse(null);
            if (owner == null) {
                throw new IllegalArgumentException("Owner with ID " + ownerId + " not found.");
            }
            pet.setOwner((Owner) owner);
        }

        if (veterinarianId != null) {
            System.out.println("this is the vet id" + veterinarianId);
            // Retrieve the vet from the database based on the owner ID
            User veterinarian = userRepository.findById(veterinarianId).orElse(null);
            if (veterinarian == null) {
                throw new IllegalArgumentException("Veterinarian with ID " + veterinarianId + " not found.");
            }
            pet.setVeterinarian((Veterinarian) veterinarian);
        }

        if (pet instanceof Cat) {
            return (Cat) petRepository.save(pet);
        } else if (pet instanceof Dog) {
            return (Dog) petRepository.save(pet);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid pet type. Only Cat and Dog are allowed.");
        }

    }


    private Dog addNewDog(Dog dog) {
        // Check if the dog with the given ID already exists
        if (dog.getId() != null && petRepository.existsById(dog.getId())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Dog already exists.");
        }
        return petRepository.save(dog);
    }


    //same method but to be used by logged in owner to create a new pet
//    public Cat addNewCatWithOwner(Cat cat, Owner owner) {
//        Cat existingCat = petRepository.findCatById(cat.getId()).orElse(null);
//        if (existingCat != null) {
//            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Cat already exists.");
//        }
//        cat.setOwner(owner);
//        return petRepository.save(cat);
//    }

    public List<Pet> findAllPets(){
        List<Pet> pets = petRepository.findAll();
        if(pets.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No pets found.");
        } else {
            return pets;
        }
    }

    public void updatePet(Long id, Pet pet){
        if (pet instanceof Cat) {
            updateCat(id, (Cat) pet);
        } else if (pet instanceof Dog) {
            updateDog(id, (Dog) pet);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid pet type. Only Cat and Dog are allowed.");
        }
    }

    private void updateCat(Long id, Cat cat){
        Cat existingCat = petRepository.findCatById(id).orElseThrow( ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cat not found."));
        cat.setId(id);
        petRepository.save(cat);
    }

    private void updateDog(Long id, Dog dog){
        Dog existingDog = petRepository.findDogById(id).orElseThrow( ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dog not found."));
        dog.setId(id);
        petRepository.save(dog);
    }
}
