package com.pethealth.finalproject.security.services.impl;

import com.pethealth.finalproject.dtos.PetReadDTO;
import com.pethealth.finalproject.model.Admin;
import com.pethealth.finalproject.model.Owner;
import com.pethealth.finalproject.model.Pet;
import com.pethealth.finalproject.model.Veterinarian;
import com.pethealth.finalproject.security.dtos.AdminDTO;
import com.pethealth.finalproject.security.dtos.OwnerDTO;
import com.pethealth.finalproject.security.dtos.UserDTO;
import com.pethealth.finalproject.security.dtos.VeterinarianDTO;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserServiceInterface, UserDetailsService {


    @Autowired
    private UserRepository userRepository;


    @Autowired
    private RoleRepository roleRepository;

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
//        User user = userRepository.findByUsername(username);
        Optional<User> userOptional = userRepository.findByUsername(username);
        // Check if user exists
        if (userOptional.isEmpty()) {
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        } else {
            User user = userOptional.get();
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
        if(userRepository.findByUsername(owner.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Username already exists");
        }
        if (userRepository.findOwnerByEmail(owner.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Email already exists");
        }
        log.info("Saving new owner {} to the database", owner.getName());
        owner.setPassword(passwordEncoder.encode(owner.getPassword()));
        owner = userRepository.save(owner);
        //el rol por defecto de owner
        if (owner.getRoles() == null || owner.getRoles().isEmpty()) {
            addRoleToUser(owner.getUsername(), "ROLE_USER");
        }
        return owner;
    }

    @Override
    public Veterinarian saveVeterinarian(Veterinarian veterinarian) {
        if (veterinarian == null) {
            throw new IllegalArgumentException("Veterinarian object cannot be null");
        }
        if(userRepository.findByUsername(veterinarian.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Username already exists");
        }
        if (userRepository.findVetByEmail(veterinarian.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Email already exists");
        }


        log.info("Saving new veterinarian {} to the database", veterinarian.getName());
        veterinarian.setPassword(passwordEncoder.encode(veterinarian.getPassword()));
        veterinarian = userRepository.save(veterinarian);
        //el rol por defecto de veterinario
        if (veterinarian.getRoles() == null || veterinarian.getRoles().isEmpty()) {
            addRoleToUser(veterinarian.getUsername(), "ROLE_VET");
        }
        return veterinarian;
    }

    @Override
    public Admin saveAdmin(Admin admin) {
        if (admin == null) {
            throw new IllegalArgumentException("Admin object cannot be null");
        }
        if(userRepository.findByUsername(admin.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Username already exists");
        }
        log.info("Saving new admin {} to the database", admin.getName());
        // Encode the user's password for security before saving
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin = userRepository.save(admin);
        if (admin.getRoles() == null || admin.getRoles().isEmpty()) {
            addRoleToUser(admin.getUsername(), "ROLE_VET");
            addRoleToUser(admin.getUsername(), "ROLE_USER");
            addRoleToUser(admin.getUsername(), "ROLE_ADMIN");
        }
        return admin;
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
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Username not found"));
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
        return userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Username not found"));
    }

    /**
     * Retrieves all users from the database
     *
     * @return a list of all users
     */
//    @Override
//    public List<User> getUsers() {
//        log.info("Fetching all users");
//        return userRepository.findAll();
//    }

    @Transactional
    @Override
    public List<UserDTO> getUsers() {
        log.info("Fetching all users");
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOs = new ArrayList<>();

        for (User user : users) {
            if(user instanceof Owner){
                Owner owner = (Owner) user;
                OwnerDTO ownerDTO = new OwnerDTO();
//                ownerDTO.setId(owner.getId());
                ownerDTO.setName(owner.getName());
                ownerDTO.setUsername(owner.getUsername());
                ownerDTO.setEmail(owner.getEmail());
                ownerDTO.setOwnedPets(toPetReadDTO(owner.getOwnedPets()));
                userDTOs.add(ownerDTO);
            } else if(user instanceof Veterinarian){
                Veterinarian vet = (Veterinarian) user;
                VeterinarianDTO vetDTO = new VeterinarianDTO();
//                vetDTO.setId(vet.getId());
                vetDTO.setName(vet.getName());
                vetDTO.setUsername(vet.getUsername());
                vetDTO.setEmail(vet.getEmail());
                vetDTO.setTreatedPets(toPetReadDTO(vet.getTreatedPets()));
                userDTOs.add(vetDTO);
            } else if(user instanceof Admin){
                Admin admin = (Admin) user;
                AdminDTO adminDTO = new AdminDTO();
//                adminDTO.setId(admin.getId());
                adminDTO.setName(admin.getName());
                adminDTO.setUsername(admin.getUsername());
                userDTOs.add(adminDTO);
            }
        }
        return userDTOs;
    }

    private List<PetReadDTO> toPetReadDTO(Set<Pet> pets) {
        List<PetReadDTO> petReadDTOs = new ArrayList<>();
        for (Pet pet : pets) {
            PetReadDTO petReadDTO = new PetReadDTO();
//            petReadDTO.setId(pet.getId());
            petReadDTO.setName(pet.getName());
            petReadDTO.setDateOfBirth(pet.getDateOfBirth());
            petReadDTO.setOwnerId(pet.getOwner().getId());
            petReadDTO.setOwnerName(pet.getOwner().getName());
            if(pet.getVeterinarian() != null) {
                petReadDTO.setVeterinarianId(pet.getVeterinarian().getId());
                petReadDTO.setVeterinarianName(pet.getVeterinarian().getName());
            } else {
                petReadDTO.setVeterinarianId(null);
                petReadDTO.setVeterinarianName(null);
            }
            petReadDTOs.add(petReadDTO);
        }
        return petReadDTOs;
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
                String encodedPassword = passwordEncoder.encode(password);
                owner.setPassword(encodedPassword);
            }
            if(email != null){
                ((Owner) owner).setEmail(email);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON. Expected Owner.");
        }
        updateOwnerInDB((Owner) owner);
    }

    //para poder actulizar cualquier campo y que no salte que el owner ya existe
    public Owner updateOwnerInDB(Owner owner) {
        if (owner == null) {
            throw new IllegalArgumentException("Owner object cannot be null");
        }
        return userRepository.save(owner);
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
                String encodedPassword = passwordEncoder.encode(password);
                veterinarian.setPassword(encodedPassword);
            }
            if(email != null){
                ((Veterinarian) veterinarian).setEmail(email);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON. Expected Veterinarian.");
        }
        updateVeterinarianInDB((Veterinarian) veterinarian);
    }

    public Veterinarian updateVeterinarianInDB(Veterinarian veterinarian) {
        if (veterinarian == null) {
            throw new IllegalArgumentException("Veterinarian object cannot be null");
        }
        return userRepository.save(veterinarian);
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
                String encodedPassword = passwordEncoder.encode(password);
                admin.setPassword(encodedPassword);
            }

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON. Expected Admin.");
        }
        updateAdminInDB((Admin) admin);
    }

    public Admin updateAdminInDB(Admin admin) {
        if (admin == null) {
            throw new IllegalArgumentException("Admin object cannot be null");
        }
        return userRepository.save(admin);
    }

    @Transactional
    public void deleteOwner(Long id){
        Owner owner = (Owner) userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found."));
        userRepository.deleteById(id);
    }

    @Transactional
    public void deleteVeterinarian(Long id){
        Veterinarian vet = (Veterinarian) userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veterinarian not found."));
        if (!vet.getTreatedPets().isEmpty()) {
            userRepository.removeAssociationVeterinarianWithPet(vet);
        }
        userRepository.deleteById(id);
    }

    public void deleteAdmin(Long id){
        Admin admin = (Admin) userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found."));
        userRepository.deleteById(id);
    }

}
