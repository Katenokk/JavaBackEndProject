package com.pethealth.finalproject.service;

import com.pethealth.finalproject.model.*;
import com.pethealth.finalproject.repository.PetRepository;
import com.pethealth.finalproject.security.models.User;
import com.pethealth.finalproject.security.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;
    public Pet findPetById(Long id){
        return petRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found"));
    }

    public Pet addNewPet(PetDTO petDTO) {
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

        return petRepository.save(pet);
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



    public List<Pet> findAllPets(){
        List<Pet> pets = petRepository.findAll();
        if(pets.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No pets found.");
        } else {
            return pets;
        }
    }

//    public void updatePet(Long id, Pet pet){
//        if (pet instanceof Cat) {
//            updateCat(id, (Cat) pet);
//        } else if (pet instanceof Dog) {
//            updateDog(id, (Dog) pet);
//        } else {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid pet type. Only Cat and Dog are allowed.");
//        }
//    }

//    private void updateCat(Long id, Cat cat){
//        Cat existingCat = petRepository.findCatById(id).orElseThrow( ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cat not found."));
//        cat.setId(id);
//        petRepository.save(cat);
//    }
//
//    private void updateDog(Long id, Dog dog){
//        Dog existingDog = petRepository.findDogById(id).orElseThrow( ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dog not found."));
//        dog.setId(id);
//        petRepository.save(dog);
//    }

    public void updatePet(Long id, PetDTO petDTO) {
        Pet existingPet = petRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found."));

        Pet updatedPet;
        if (petDTO instanceof CatDTO) {
            updatedPet = mapToCatEntity((CatDTO) petDTO);
        } else if (petDTO instanceof DogDTO) {
            updatedPet = mapToDogEntity((DogDTO) petDTO);
        } else {
            throw new IllegalArgumentException("Invalid pet type.");
        }

        updatedPet.setId(id);

        petRepository.save(updatedPet);
    }

    public void partialUpdate(Long id, PetDTO petDTO){
        Pet existingPet = petRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found."));
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
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found."));

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
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found."));
        Veterinarian veterinarian = (Veterinarian) userRepository.findById(vetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veterinarian not found."));

        pet.setVeterinarian(veterinarian);
        if(veterinarian.getTreatedPets() == null){
            veterinarian.setTreatedPets(new HashSet<>());
        }
        veterinarian.getTreatedPets().add(pet);

        userRepository.save(veterinarian);
        petRepository.save(pet);

        return userRepository.findById(vetId).map(user -> {
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
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found."));
        Veterinarian veterinarian = (Veterinarian) userRepository.findById(vetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veterinarian not found."));

        veterinarian.getTreatedPets().remove(pet);
        pet.setVeterinarian(null);
        return pet;
    }
}
