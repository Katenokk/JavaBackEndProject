package com.pethealth.finalproject.controller;

import com.pethealth.finalproject.model.Cat;
import com.pethealth.finalproject.model.Dog;
import com.pethealth.finalproject.model.Pet;
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

    @PostMapping("pets/dogs")
    @ResponseStatus(HttpStatus.CREATED)
    public Dog addNewPet(@RequestBody @Valid Dog dog) {
        return (Dog) petService.addNewPet(dog);
    }
//falta patch, put, delete
    //findByalgo breed, disease

}
