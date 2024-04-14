package com.pethealth.finalproject.service;

import com.pethealth.finalproject.model.Cat;
import com.pethealth.finalproject.model.Dog;
import com.pethealth.finalproject.model.Pet;
import com.pethealth.finalproject.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

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
        Cat existingCat = petRepository.findCatById(cat.getId())
                .orElse(null);
        if (existingCat != null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Cat already exists.");
        }
        return petRepository.save(cat);
    }

    private Dog addNewDog(Dog dog) {
        // Check if the dog with the given ID already exists
        if (dog.getId() != null && petRepository.existsById(dog.getId())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Dog already exists.");
        }
        return petRepository.save(dog);
    }

    public List<Pet> findAllPets(){
        List<Pet> pets = petRepository.findAll();
        if(pets.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No pets found.");
        } else {
            return pets;
        }
    }
}
