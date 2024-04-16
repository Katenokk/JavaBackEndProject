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

    @PostMapping("/pets")
    @ResponseStatus(HttpStatus.CREATED)
    public Pet addPetPrueba(@RequestBody @Valid Pet pet) {
        return petService.addPetprobandoo(pet);
    }

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


    @PutMapping("pets/cats/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updatePet(@PathVariable Long id, @RequestBody Cat cat){
        petService.updatePet(id, cat);
    }

    @PutMapping("pets/dogs/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updatePet(@PathVariable Long id, @RequestBody Dog dog){
        petService.updatePet(id, dog);
    }
//falta patch, delete
    //findByalgo breed, disease

}
