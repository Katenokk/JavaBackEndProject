package com.pethealth.finalproject.security.services.impl;

import com.pethealth.finalproject.model.Admin;
import com.pethealth.finalproject.model.Owner;
import com.pethealth.finalproject.model.Veterinarian;
import com.pethealth.finalproject.security.models.Role;
import com.pethealth.finalproject.security.models.User;
import com.pethealth.finalproject.security.repositories.RoleRepository;
import com.pethealth.finalproject.security.repositories.UserRepository;
import com.pethealth.finalproject.security.services.interfaces.UserServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserServiceInterface, UserDetailsService {

    /**
     * Autowired UserRepository for database operations.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Autowired RoleRepository for database operations.
     */
    @Autowired
    private RoleRepository roleRepository;

    /**
     * Injects a bean of type PasswordEncoder into this class.
     * The bean is used for encoding passwords before storing them.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Loads the user by its username
     *
     * @param username the username to search for
     * @return the UserDetails object that matches the given username
     * @throws UsernameNotFoundException if the user with the given username is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Retrieve user with the given username
        User user = userRepository.findByUsername(username);
        // Check if user exists
        if (user == null) {
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        } else {
            log.info("User found in the database: {}", username);
            // Create a collection of SimpleGrantedAuthority objects from the user's roles
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            user.getRoles().forEach(role -> {
                authorities.add(new SimpleGrantedAuthority(role.getName()));
            });
            // Return the user details, including the username, password, and authorities
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
        }
    }

    /**
     * Saves a new user to the database
     *
     * @param user the user to be saved
     * @return the saved user
     */
    @Override
    public User saveUser(User user) {
        log.info("Saving new user {} to the database", user.getName());
        // Encode the user's password for security before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public Owner saveOwner(Owner owner) {
        if (owner == null) {
            throw new IllegalArgumentException("Owner object cannot be null");
        }
        log.info("Saving new owner {} to the database", owner.getName());
        owner.setPassword(passwordEncoder.encode(owner.getPassword()));
        return userRepository.save(owner);
    }

    @Override
    public Veterinarian saveVeterinarian(Veterinarian veterinarian) {
        log.info("Saving new veterinarian {} to the database", veterinarian.getName());
        veterinarian.setPassword(passwordEncoder.encode(veterinarian.getPassword()));
        return userRepository.save(veterinarian);
    }

    @Override
    public Admin saveAdmin(Admin admin) {
        log.info("Saving new admin {} to the database", admin.getName());
        // Encode the user's password for security before saving
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return userRepository.save(admin);
    }

    /**
     * Saves a new role to the database
     *
     * @param role the role to be saved
     * @return the saved role
     */
    @Override
    public Role saveRole(Role role) {
        log.info("Saving new role {} to the database", role.getName());
        return roleRepository.save(role);
    }

    /**
     * Adds a role to the user with the given username
     *
     * @param username the username of the user to add the role to
     * @param roleName the name of the role to be added
     */
    @Override
    public void addRoleToUser(String username, String roleName) {
        log.info("Adding role {} to user {}", roleName, username);

        // Retrieve the user and role objects from the repository
        User user = userRepository.findByUsername(username);
        Role role = roleRepository.findByName(roleName);

        // Add the role to the user's role collection
        user.getRoles().add(role);

        // Save the user to persist the changes
        userRepository.save(user);
    }

    /**
     * Retrieves the user with the given username
     *
     * @param username the username to search for
     * @return the user with the given username
     */
    @Override
    public User getUser(String username) {
        log.info("Fetching user {}", username);
        return userRepository.findByUsername(username);
    }

    /**
     * Retrieves all users from the database
     *
     * @return a list of all users
     */
    @Override
    public List<User> getUsers() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }

    public void updateOwner(Long id, Owner owner){
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found."));
        if(existingUser instanceof Owner){
            owner.setId(id);
            saveOwner(owner);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON. Expected Owner.");
        }
    }

    public void updateVeterinarian(Long id, Veterinarian veterinarian){
        User existingVet = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veterinarian not found."));
        if(existingVet instanceof  Veterinarian){
            veterinarian.setId(id);
            saveVeterinarian(veterinarian);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON. Expected Veterinarian.");
        }
    }

    public void updateAdmin(Long id, Admin admin){
        User existingAdmin = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found."));
        if(existingAdmin instanceof Admin){
            admin.setId(id);
            saveAdmin(admin);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON. Expected Admin.");
        }
    }

    //put de varios campos a la vez
    public void partialUpdateOwner(Long id, String name, String username, String password, String email){
        User owner = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found."));
        if(owner instanceof Owner){
            if(name != null){
                owner.setName(name);
            }
            if(username != null){
                owner.setUsername(username);
            }
            if(password != null){
                owner.setPassword(password);
            }
            if(email != null){
                ((Owner) owner).setEmail(email);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON. Expected Owner.");
        }
        saveOwner((Owner) owner);
    }

    public void partialUpdateVeterinarian(Long id, String name, String username, String password, String email){
        User veterinarian = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veterinarian not found."));
        if(veterinarian instanceof Veterinarian){
            if(name != null){
                veterinarian.setName(name);
            }
            if(username != null){
                veterinarian.setUsername(username);
            }
            if(password != null){
                veterinarian.setPassword(password);
            }
            if(email != null){
                ((Veterinarian) veterinarian).setEmail(email);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON. Expected Veterinarian.");
        }
        saveVeterinarian((Veterinarian) veterinarian);
    }

    public void partialUpdateAdmin(Long id, String name, String username, String password){
        User admin = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found."));
        if(admin instanceof Admin){
            if(name != null){
                admin.setName(name);
            }
            if(username != null){
                admin.setUsername(username);
            }
            if(password != null){
                admin.setPassword(password);
            }

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON. Expected Admin.");
        }
        saveAdmin((Admin) admin);
    }

    public void deleteOwner(Long id){
        Owner owner = (Owner) userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found."));
        userRepository.deleteById(id);
    }

    public void deleteVeterinarian(Long id){
        Veterinarian vet = (Veterinarian) userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veterinarian not found."));
        userRepository.removeAssociationVeterinarianWithPet(vet);
        userRepository.deleteById(id);
    }

    public void deleteAdmin(Long id){
        Admin admin = (Admin) userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found."));
        userRepository.deleteById(id);
    }

}
