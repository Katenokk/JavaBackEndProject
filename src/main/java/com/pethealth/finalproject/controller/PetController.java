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


    @PostMapping("/pets")
    public ResponseEntity<Pet> addNewPet(@RequestBody PetDTO petDTO) {
        return ResponseEntity.ok(petService.addNewPet(petDTO));
    }


//    @PutMapping("pets/cats/{id}")
//    @ResponseStatus(value = HttpStatus.NO_CONTENT)
//    public void updatePet(@PathVariable Long id, @RequestBody Cat cat){
//        petService.updatePet(id, cat);
//    }
//
//    @PutMapping("pets/dogs/{id}")
//    @ResponseStatus(value = HttpStatus.NO_CONTENT)
//    public void updatePet(@PathVariable Long id, @RequestBody Dog dog){
//        petService.updatePet(id, dog);
//    }

    @PutMapping("pets/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updatePets(@PathVariable Long id, @RequestBody PetDTO petDTO){
        petService.updatePet(id, petDTO);
    }

    @PatchMapping("pets/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void patchPets(@PathVariable Long id, @RequestBody PetDTO petDTO){
        petService.partialUpdate(id, petDTO);
    }

    @DeleteMapping("pets/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public ResponseEntity<String> deletePet(@PathVariable Long id){
        petService.deletePet(id);
        return ResponseEntity.ok("Pet deleted successfully.");
    }

    @PatchMapping("/pets/veterinarians/{petId}/{vetId}")
    public ResponseEntity<String> assignVeterinarianToPet(@PathVariable Long petId, @PathVariable Long vetId) {
        petService.addVeterinarianToPet(petId, vetId);
        return ResponseEntity.ok("Veterinarian assigned to pet successfully.");
    }

    @DeleteMapping("/pets/{petId}/veterinarians/{vetId}")
    public ResponseEntity<String> removeVeterinarianFromPet(@PathVariable Long petId, @PathVariable Long vetId) {
        petService.removeVeterinarianFromPet(petId, vetId);
        return ResponseEntity.ok("Veterinarian removed from pet successfully.");
    }



    //findByalgo breed, disease

}
