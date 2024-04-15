package com.pethealth.finalproject.controller;

import com.pethealth.finalproject.model.Cat;
import com.pethealth.finalproject.model.Dog;
import com.pethealth.finalproject.model.Owner;
import com.pethealth.finalproject.model.Pet;
import com.pethealth.finalproject.security.models.User;
import com.pethealth.finalproject.security.services.impl.UserService;
import com.pethealth.finalproject.service.PetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController //mejor que controller??
@RequestMapping("/api")
public class PetController {

    @Autowired
    private PetService petService;

    @Autowired
    private UserService userService;

    @GetMapping("/pets/{id}")
    public Pet findPetById(@PathVariable Long id){
        return petService.findPetById(id);
    }

    @GetMapping("/pets")
    public List<Pet> findAllDoctors() {
        return petService.findAllPets();
    }


//PARA ESTO hace falta un discriminador en el json para que spring sepa que tipo de Pet es
//    @PostMapping("/pets")
//    @ResponseStatus(HttpStatus.CREATED)
//    public Pet addNewPet(@RequestBody @Valid Pet pet) {
//        return petService.addNewPet(pet);
//    }

    @PostMapping("/pets/cats")
    @ResponseStatus(HttpStatus.CREATED)
    public Cat addNewPet(@RequestBody @Valid Cat cat) {
        return (Cat) petService.addNewPet(cat);
    }

//    @PostMapping("/pets/cats")
//    @ResponseStatus(HttpStatus.CREATED)
//    public Cat addNewPet(@RequestBody @Valid Cat cat, @RequestParam(required = false) String username) {
//        // Check if an ownerId is provided
//        if (username != null) {
//            // Retrieve the owner by ID from the database
//            User user = userService.getUser(username);
//
//            // Check if the user is an instance of Owner
//            if (user instanceof Owner) {
//                // Set the owner of the cat
//                cat.setOwner((Owner) user);
//            } else {
//                // Handle the case where the retrieved user is not an Owner
//                throw new IllegalArgumentException("User with username " + username + " is not an Owner.");
//            }
//        }
//
//        // Add the cat (with or without owner) using the existing addNewPet method
//        return (Cat) petService.addNewPet(cat);
//    }




    @PostMapping("pets/dogs")
    @ResponseStatus(HttpStatus.CREATED)
    public Dog addNewPet(@RequestBody @Valid Dog dog) {
        return (Dog) petService.addNewPet(dog);
    }
//falta patch, put, delete
    //findByalgo breed, disease

}
