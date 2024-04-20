package com.pethealth.finalproject.controller;

import com.pethealth.finalproject.model.*;
import com.pethealth.finalproject.security.models.User;
import com.pethealth.finalproject.security.services.impl.UserService;
import com.pethealth.finalproject.service.PetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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



//no funciona bien la deserializacion

    @PostMapping("/pets")
    public ResponseEntity<Pet> addNewPet(@RequestBody PetDTO petDTO) {
//        if ("cat".equals(petDTO.getDiscriminator())) {
//            System.out.println(petDTO.getDiscriminator());
//            return ResponseEntity.ok(petService.addNewPet2((CatDTO) petDTO));
//        } else if ("dog".equals(petDTO.getDiscriminator())) {
//            return ResponseEntity.ok(petService.addNewPet2((DogDTO) petDTO));
//        } else {
//            throw new IllegalArgumentException("Invalid pet type.");
//        }
        //de esta forma no se puede hacer el cast
        return ResponseEntity.ok(petService.addNewPet2(petDTO));
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

    //cambiar update para que use petDTO?
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
