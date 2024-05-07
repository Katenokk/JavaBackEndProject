package com.pethealth.finalproject.security.services.interfaces;

import com.pethealth.finalproject.model.Admin;
import com.pethealth.finalproject.model.Owner;
import com.pethealth.finalproject.model.Veterinarian;
import com.pethealth.finalproject.security.dtos.UserDTO;
import com.pethealth.finalproject.security.models.Role;
import com.pethealth.finalproject.security.models.User;

import java.util.List;

/**
 * The UserServiceInterface is an interface that defines the methods that are available to perform operations on User entities.
 */
public interface UserServiceInterface {

    /**
     * This method is used to save a User entity to the database.
     *
     * @param user the User entity to be saved.
     * @return the saved User entity.
     */
    User saveUser(User user);

    /**
     * This method is used to save a Role entity to the database.
     *
     * @param role the Role entity to be saved.
     * @return the saved Role entity.
     */
    Role saveRole(Role role);

    /**
     * This method is used to add a Role to a User.
     *
     * @param username the username of the User to which the Role is to be added.
     * @param roleName the name of the Role to be added.
     */
    void addRoleToUser(String username, String roleName);

    /**
     * This method is used to retrieve a User from the database by its username.
     *
     * @param username the username of the User to be retrieved.
     * @return the retrieved User entity.
     */
    User getUser(String username);

    /**
     * This method is used to retrieve all User entities from the database.
     *
     * @return a List of all User entities.
     */
//    List<User> getUsers();

    List<UserDTO> getUsers();


    Owner saveOwner(Owner owner);

    Veterinarian saveVeterinarian(Veterinarian veterinarian);

    Admin saveAdmin(Admin admin);

    void updateOwner(Long id, Owner owner);

    void updateAdmin(Long id, Admin admin);

    void updateVeterinarian(Long id, Veterinarian veterinarian);

    void partialUpdateOwner(Long id, String name, String username, String password, String email);

    void partialUpdateVeterinarian(Long id, String name, String username, String password, String email);

    void partialUpdateAdmin(Long id, String name, String username, String password);

    void deleteOwner(Long id);

    void deleteVeterinarian(Long id);

    void deleteAdmin(Long id);


    List<Veterinarian> getAllVeterinarians();
}
