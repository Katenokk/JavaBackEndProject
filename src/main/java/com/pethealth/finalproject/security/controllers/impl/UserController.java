package com.pethealth.finalproject.security.controllers.impl;

import com.pethealth.finalproject.model.Admin;
import com.pethealth.finalproject.model.Owner;
import com.pethealth.finalproject.model.Veterinarian;
import com.pethealth.finalproject.security.dtos.OwnerDTO;
import com.pethealth.finalproject.security.dtos.VeterinarianDTO;
import com.pethealth.finalproject.security.models.User;
import com.pethealth.finalproject.security.services.interfaces.UserServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RESTful API for User management
 */
@RestController
@RequestMapping("/api")
public class UserController {

    /**
     * User service for accessing user data
     */
    @Autowired
    private UserServiceInterface userService;

    /**
     * Get a list of all users
     *
     * @return list of all users
     */
    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getUsers() {
        return userService.getUsers();
    }

    /**
     * Save a new user
     *
     * @param user the user to be saved
     */
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveUser(@RequestBody User user) {
        userService.saveUser(user);
    }

    @PostMapping("/owners")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveOwner(@RequestBody Owner owner) {
        userService.saveUser(owner);
    }

    @PostMapping("/admins")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveAdmin(@RequestBody Admin admin) {
        userService.saveUser(admin);
    }

    @PostMapping("/veterinarians")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveVeterinarian(@RequestBody Veterinarian veterinarian) {
        userService.saveUser(veterinarian);
    }



    //no se pone la id en el json!
    @PutMapping("/admins/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateAdmin(@PathVariable Long id, @RequestBody Admin admin){
        userService.updateAdmin(id, admin);
    }

    @PutMapping("/owners/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateOwner(@PathVariable Long id, @RequestBody Owner owner) {
        userService.updateOwner(id, owner);
    }

    @PutMapping("/veterinarians/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateVeterinarian(@PathVariable Long id, @RequestBody Veterinarian veterinarian) {
        userService.updateVeterinarian(id, veterinarian);
    }

    @PatchMapping("/owners/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void partialUpdateOwner(@PathVariable Long id, @RequestBody OwnerDTO ownerDTO) {
        userService.partialUpdateOwner(id, ownerDTO.getName(), ownerDTO.getUsername(), ownerDTO.getPassword(), ownerDTO.getEmail());
    }

    @PatchMapping("/veterinarians/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void partialUpdateVeterinarian(@PathVariable Long id, @RequestBody VeterinarianDTO veterinarianDTO) {
        userService.partialUpdateVeterinarian(id, veterinarianDTO.getName(), veterinarianDTO.getUsername(), veterinarianDTO.getPassword(), veterinarianDTO.getEmail());
    }
}
