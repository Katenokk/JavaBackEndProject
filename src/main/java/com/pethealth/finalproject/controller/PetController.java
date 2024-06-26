package com.pethealth.finalproject.controller;

import com.pethealth.finalproject.dtos.PetReadDTO;
import com.pethealth.finalproject.model.Pet;
import com.pethealth.finalproject.model.PetDTO;
import com.pethealth.finalproject.security.services.impl.UserService;
import com.pethealth.finalproject.service.PetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PetController {

    @Autowired
    private PetService petService;

    @Autowired
    private UserService userService;

    @GetMapping("/pets/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Pet findPetById(@PathVariable Long id){
        return petService.findPetById(id);
    }

    //endpoint aparte solo para admins
    @GetMapping("/pets")
    @ResponseStatus(HttpStatus.OK)
    public List<PetReadDTO> findAllPets() {
        return petService.findAllPets();
    }

    @PostMapping("/pets")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Pet> addNewPet(@RequestBody @Valid PetDTO petDTO) {
        return ResponseEntity.ok(petService.addNewPet(petDTO));
    }

    @PutMapping("pets/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updatePets(@PathVariable Long id, @RequestBody @Valid PetDTO petDTO){
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

    @DeleteMapping("/pets/veterinarians/{petId}/{vetId}")
    public ResponseEntity<String> removeVeterinarianFromPet(@PathVariable Long petId, @PathVariable Long vetId) {
        petService.removeVeterinarianFromPet(petId, vetId);
        return ResponseEntity.ok("Veterinarian removed from pet successfully.");
    }

    @GetMapping("/pets/veterinarians")
    public List<PetReadDTO> findAllPetsByVeterinarian() {
        return petService.findAllPetsByVeterinarian();
    }

    @GetMapping("/pets/owners")
    public List<PetReadDTO> findAllPetsByOwner() {
        return petService.findAllPetsByOwner();
    }


}
