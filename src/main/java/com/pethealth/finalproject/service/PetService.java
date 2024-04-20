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

    public Pet addNewPet2(PetDTO petDTO) {

        Pet pet;

        if ((petDTO instanceof CatDTO)) {
            pet = mapToCatEntity((CatDTO) petDTO);
        } else if ((petDTO instanceof DogDTO)){
            pet = mapToDogEntity((DogDTO) petDTO);
        } else {
            throw new IllegalArgumentException("Invalid pet type.");
        }

        if (pet instanceof Cat) {
            Cat existingCat = petRepository.findCatById(pet.getId()).orElse(null);
            if (existingCat != null) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Cat already exists.");
            }
        } else if (pet instanceof Dog) {
            Dog existingDog = petRepository.findDogById(pet.getId()).orElse(null);
            if (existingDog != null) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Dog already exists.");
            }
        } else {
            throw new IllegalArgumentException("Invalid pet type.");
        }

        // Handle other logic based on the pet type
//        if (pet.getOwner() != null) {
//            // Retrieve the owner from the database based on the owner ID
//            User owner = userRepository.findById(pet.getOwner().getId()).orElse(null);
//            if (owner == null) {
//                throw new IllegalArgumentException("Owner not found.");
//            }
//            pet.setOwner((Owner) owner);
//        }

//        if (pet instanceof Cat && ((Cat) pet).getVeterinarian() != null) {
//            // Retrieve the vet from the database based on the ID
//            User veterinarian = userRepository.findById(((Cat) pet).getVeterinarian().getId()).orElse(null);
//            if (veterinarian == null) {
//                throw new IllegalArgumentException("Veterinarian not found.");
//            }
//            ((Cat) pet).setVeterinarian((Veterinarian) veterinarian);
//        }

        return petRepository.save(pet);
    }


    private Cat mapToCatEntity(CatDTO catDTO) {
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

        Owner owner = (Owner) userRepository.findById(dogDTO.getOwner().getId())
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));
        dog.setOwner(owner);


        Veterinarian veterinarian = (Veterinarian) userRepository.findById(dogDTO.getVeterinarian().getId())
                .orElseThrow(() -> new IllegalArgumentException("Veterinarian not found"));
        dog.setVeterinarian(veterinarian);

        dog.setOwner(dogDTO.getOwner());
        dog.setVeterinarian(dogDTO.getVeterinarian());
        return dog;
    }






    private Dog addNewDog(Dog dog) {
        // Check if the dog with the given ID already exists
        if (dog.getId() != null && petRepository.existsById(dog.getId())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Dog already exists.");
        }
        return petRepository.save(dog);
    }

//    public PetDTO addNewPet(PetDTO petDTO) {
//        if (petDTO instanceof CatDTO) {
//            // Logic to add a new cat
//        } else if (petDTO instanceof DogDTO) {
//            // Logic to add a new dog
//        } else {
//            throw new IllegalArgumentException("Invalid pet type.");
//        }
//    }


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
