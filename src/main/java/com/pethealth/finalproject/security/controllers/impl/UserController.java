package com.pethealth.finalproject.security.controllers.impl;

import com.pethealth.finalproject.model.Admin;
import com.pethealth.finalproject.model.Owner;
import com.pethealth.finalproject.model.Veterinarian;
import com.pethealth.finalproject.security.dtos.*;
import com.pethealth.finalproject.security.models.User;
import com.pethealth.finalproject.security.services.interfaces.UserServiceInterface;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
//    @GetMapping("/users")
//    @ResponseStatus(HttpStatus.OK)
//    public List<User> getUsers() {
//        return userService.getUsers();
//    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDTO> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/users/username")
    @ResponseStatus(HttpStatus.OK)
    public User finUserByUsername(@RequestParam String username) {
        return userService.getUser(username);
    }



    /**
     * Save a new user
     *
     * @param user the user to be saved
     */
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveUser(@RequestBody @Valid User user) {
        userService.saveUser(user);
    }

    @PostMapping("/owners")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveOwner(@RequestBody Owner owner) {
        userService.saveOwner(owner);
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

    //registro de usuarios:

    @PostMapping("/register/owners")
    public ResponseEntity<String> registerOwner(@RequestBody Owner owner) {
        try {
            userService.saveOwner(owner);
            return ResponseEntity.ok("Owner registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error registering owner: " + e.getMessage());
        }
    }

    @PostMapping("/register/veterinarians")
    public ResponseEntity<String> registerVeterinarian(@RequestBody Veterinarian veterinarian) {
        try {
            userService.saveVeterinarian(veterinarian);
            return ResponseEntity.ok("Veterinarian registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error registering veterinarian: " + e.getMessage());
        }
    }

    @PostMapping("/register/admins")
    public ResponseEntity<String> registerAdmin(@RequestBody Admin admin) {
        try {
            userService.saveAdmin(admin);
            return ResponseEntity.ok("Admin registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error registering admin: " + e.getMessage());
        }
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
    public void partialUpdateOwner(@PathVariable Long id, @RequestBody UserUpdateDTO userUpdateDTO) {
        userService.partialUpdateOwner(id, userUpdateDTO.getName(), userUpdateDTO.getUsername(), userUpdateDTO.getPassword(), userUpdateDTO.getEmail());
    }

    @PatchMapping("/veterinarians/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void partialUpdateVeterinarian(@PathVariable Long id, @RequestBody UserUpdateDTO userUpdateDTO) {
        userService.partialUpdateVeterinarian(id, userUpdateDTO.getName(), userUpdateDTO.getUsername(), userUpdateDTO.getPassword(), userUpdateDTO.getEmail());
    }

    @PatchMapping("/admins/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void partialUpdateAdmin(@PathVariable Long id, @RequestBody UserUpdateDTO userUpdateDTO) {
        userService.partialUpdateAdmin(id, userUpdateDTO.getName(), userUpdateDTO.getUsername(),  userUpdateDTO.getPassword());
    }




    @DeleteMapping("/owners/{id}")
    public ResponseEntity<String> deleteOwnerById(@PathVariable(name="id") Long id){
        userService.deleteOwner(id);
        return ResponseEntity.ok("Owner deleted successfully");
    }

    @Transactional
    @DeleteMapping("/veterinarians/{id}")
    public ResponseEntity<String> deleteVeterinarianById(@PathVariable(name="id") Long id){
        userService.deleteVeterinarian(id);
        return ResponseEntity.ok("Veterinarian deleted successfully");
    }

    @DeleteMapping("/admins/{id}")
    public ResponseEntity<String> deleteAdminById(@PathVariable(name="id") Long id){
        userService.deleteAdmin(id);
        return ResponseEntity.ok("Admin deleted successfully");
    }
}
