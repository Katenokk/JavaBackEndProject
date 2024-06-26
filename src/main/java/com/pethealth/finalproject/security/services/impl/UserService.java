package com.pethealth.finalproject.security.services.impl;

import com.pethealth.finalproject.dtos.PetReadDTO;
import com.pethealth.finalproject.model.Admin;
import com.pethealth.finalproject.model.Owner;
import com.pethealth.finalproject.model.Pet;
import com.pethealth.finalproject.model.Veterinarian;
import com.pethealth.finalproject.repository.PetRepository;
import com.pethealth.finalproject.security.dtos.AdminDTO;
import com.pethealth.finalproject.security.dtos.OwnerDTO;
import com.pethealth.finalproject.security.dtos.UserDTO;
import com.pethealth.finalproject.security.dtos.VeterinarianDTO;
import com.pethealth.finalproject.security.models.CustomUserDetails;
import com.pethealth.finalproject.security.models.Role;
import com.pethealth.finalproject.security.models.User;
import com.pethealth.finalproject.security.repositories.RoleRepository;
import com.pethealth.finalproject.security.repositories.UserRepository;
import com.pethealth.finalproject.security.services.interfaces.UserServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private PetRepository petRepository;

    public String getCurrentUserName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            return currentUserName;
        }
        return null;
    }


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
//            return new CustomUserDetails(user.getUsername(), user.getPassword(), authorities, user.getId());
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
                ownerDTO.setName(owner.getName());
                ownerDTO.setUsername(owner.getUsername());
                ownerDTO.setEmail(owner.getEmail());
                ownerDTO.setOwnedPets(toPetReadDTO(owner.getOwnedPets()));
                userDTOs.add(ownerDTO);
            } else if(user instanceof Veterinarian){
                Veterinarian vet = (Veterinarian) user;
                VeterinarianDTO vetDTO = new VeterinarianDTO();
                vetDTO.setName(vet.getName());
                vetDTO.setUsername(vet.getUsername());
                vetDTO.setEmail(vet.getEmail());
                vetDTO.setTreatedPets(toPetReadDTO(vet.getTreatedPets()));
                userDTOs.add(vetDTO);
            } else if(user instanceof Admin){
                Admin admin = (Admin) user;
                AdminDTO adminDTO = new AdminDTO();
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



    public List<Veterinarian> getAllVeterinarians(){
        String currentUsername = getCurrentUserName();
        if(currentUsername == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to update an account.");
        }

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        // Check if the current user is the owner of the account they are trying to update
        if (!(currentUser instanceof Owner) && !(currentUser instanceof Admin)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only owners and admins can see all veterinarians.");
        }

        List<Veterinarian> allVeterinarians = new ArrayList<>();
        for(User user : userRepository.findAll()){
            if(user instanceof Veterinarian){
                allVeterinarians.add((Veterinarian) user);
            }
        }
        if(allVeterinarians.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No veterinarians found.");
        }

        return allVeterinarians;
    }

    public void updateOwner(Long id, Owner owner){
        String currentUsername = getCurrentUserName();
        if(currentUsername == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to update an account.");
        }

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found."));

        // Check if the current user is the owner of the account they are trying to update
        if (!currentUser.getId().equals(existingUser.getId()) && !(currentUser instanceof Admin)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You can only update your own account or an admin can update any account.");
        }

        if(existingUser instanceof Owner){
            Owner existingOwner = (Owner) existingUser;
            checkEmailOwner(existingOwner, owner);
            if(owner.getPassword() != null){
                String encodedPassword = passwordEncoder.encode(owner.getPassword());
                owner.setPassword(encodedPassword);
            }
            owner.setId(id);
            //ignora el array de pets
            Set<Pet> existingPets = ((Owner) existingUser).getOwnedPets();
            owner.setOwnedPets(existingPets);
            //ingora el campo username si es diferente
            owner.setUsername(existingOwner.getUsername());
            updateOwnerInDB(owner);

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON. Expected Owner.");
        }
    }



    public void updateVeterinarian(Long id, Veterinarian veterinarian){
        String currentUsername = getCurrentUserName();
        if(currentUsername == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to update an account.");
        }

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veterinarian not found."));

        // Check if the current user is the owner of the account they are trying to update
        if (!currentUser.getId().equals(existingUser.getId()) && !(currentUser instanceof Admin)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You can only update your own account or an admin can update any account.");
        }

        if(existingUser instanceof  Veterinarian){
            Veterinarian existingVeterinarian = (Veterinarian) existingUser;
            checkEmailVet(existingVeterinarian, veterinarian);
            if(veterinarian.getPassword() != null){
                String encodedPassword = passwordEncoder.encode(veterinarian.getPassword());
                veterinarian.setPassword(encodedPassword);
            }
            veterinarian.setId(id);
            //ignora el array de pets
            Set<Pet> existingPets = ((Veterinarian) existingUser).getTreatedPets();
            veterinarian.setTreatedPets(existingPets);
            //ignora el campo username si es diferente
            veterinarian.setUsername(existingVeterinarian.getUsername());
            updateVeterinarianInDB(veterinarian);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON. Expected Veterinarian.");
        }
    }
    //helpers para verificar si el email no está ya en la base de datos
    private void checkEmailVet(Veterinarian existingVeterinarian, Veterinarian veterinarian) {
        // Check if the new email already exists in the database
        if (!veterinarian.getEmail().equals(existingVeterinarian.getEmail()) && userRepository.findByEmail(veterinarian.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Email already exists");
        }
    }

    private void checkEmailOwner(Owner existingOwner, Owner owner) {
        // Check if the new email already exists in the database
        if (!owner.getEmail().equals(existingOwner.getEmail()) && userRepository.findOwnerByEmail(owner.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Email already exists");
        }
    }

    public void updateAdmin(Long id, Admin admin){
        String currentUsername = getCurrentUserName();
        if(currentUsername == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to update an account.");
        }

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found."));

        // Check if the current user is an admin
        if (!(currentUser instanceof Admin)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only an admin can update admin accounts.");
        }

        if(existingUser instanceof Admin){
            System.out.println("username!!!");
            System.out.println(admin.getUsername());
            // Check if the new username already exists in the database
            if (!admin.getUsername().equals(existingUser.getUsername()) && userRepository.findByUsername(admin.getUsername()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Username already exists");
            }
            if(admin.getPassword() != null){
                String encodedPassword = passwordEncoder.encode(admin.getPassword());
                admin.setPassword(encodedPassword);
            }
            admin.setId(id);
            admin.setUsername(existingUser.getUsername());
            updateAdminInDB(admin);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON. Expected Admin.");
        }
    }

    //patch de owner
    public void partialUpdateOwner(Long id, String name, String password, String email){
        String currentUsername = getCurrentUserName();
        if(currentUsername == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to update an account.");
        }
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found."));

        // Check if the current user is the owner of the account they are trying to update
        if (!currentUser.getId().equals(existingUser.getId()) && !(currentUser instanceof Admin)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You can only update your own account or an admin can update any account.");
        }

        if(existingUser instanceof Owner){
            Owner existingOwner = (Owner) existingUser;
            if(name != null){
                existingUser.setName(name);
            }
            if(password != null){
                String encodedPassword = passwordEncoder.encode(password);
                existingUser.setPassword(encodedPassword);
            }
            if(email != null){
                if (!email.equals(existingOwner.getEmail()) && userRepository.findOwnerByEmail(email).isPresent()) {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Email already exists");
                }
                ((Owner) existingUser).setEmail(email);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON. Expected Owner.");
        }
        updateOwnerInDB((Owner) existingUser);
    }

    //para poder actulizar cualquier campo y que no salte que el owner ya existe
    public Owner updateOwnerInDB(Owner owner) {
        if (owner == null) {
            throw new IllegalArgumentException("Owner object cannot be null");
        }
        return userRepository.save(owner);
    }

    public void partialUpdateVeterinarian(Long id, String name, String password, String email){
        String currentUsername = getCurrentUserName();
        if(currentUsername == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to update an account.");
        }

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veterinarian not found."));

        // Check if the current user is the owner of the account they are trying to update
        if (!currentUser.getId().equals(existingUser.getId()) && !(currentUser instanceof Admin)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You can only update your own account or an admin can update any account.");
        }

        if(existingUser instanceof Veterinarian){
            Veterinarian existingVeterinarian = (Veterinarian) existingUser;
            if(name != null){
                existingUser.setName(name);
            }
            if(password != null){
                String encodedPassword = passwordEncoder.encode(password);
                existingUser.setPassword(encodedPassword);
            }
            if(email != null){
                //comprobar si ya existe
                if (!email.equals(existingVeterinarian.getEmail()) && userRepository.findByEmail(email).isPresent()) {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Email already exists");
                }
                ((Veterinarian) existingUser).setEmail(email);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON. Expected Veterinarian.");
        }
        updateVeterinarianInDB((Veterinarian) existingUser);
    }

    public Veterinarian updateVeterinarianInDB(Veterinarian veterinarian) {
        if (veterinarian == null) {
            throw new IllegalArgumentException("Veterinarian object cannot be null");
        }
        return userRepository.save(veterinarian);
    }

    public void partialUpdateAdmin(Long id, String name, String password){
        String currentUsername = getCurrentUserName();
        if(currentUsername == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to update an account.");
        }

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found."));

        // Check if the current user is an admin
        if (!(currentUser instanceof Admin)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only an admin can update admin accounts.");
        }

        if(existingUser instanceof Admin){
            Admin existingAdmin = (Admin) existingUser;
            if(name != null){
                existingUser.setName(name);
            }
            if(password != null){
                String encodedPassword = passwordEncoder.encode(password);
                existingUser.setPassword(encodedPassword);
            }

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON. Expected Admin.");
        }
        updateAdminInDB((Admin) existingUser);
    }

    public Admin updateAdminInDB(Admin admin) {
        if (admin == null) {
            throw new IllegalArgumentException("Admin object cannot be null");
        }
        return userRepository.save(admin);
    }

    @Transactional
    public void deleteOwner(Long id){
        String currentUsername = getCurrentUserName();
        if(currentUsername == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to delete an account.");
        }
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found."));

        // Check if the current user is the owner of the account they are trying to delete
        if (!currentUser.getId().equals(existingUser.getId()) && !(currentUser instanceof Admin)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You can only delete your own account or an admin can delete any account.");
        }

        userRepository.deleteById(id);
    }

    @Transactional
    public void deleteVeterinarian(Long id){
        String currentUsername = getCurrentUserName();
        if(currentUsername == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to delete an account.");
        }

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veterinarian not found."));

        // Check if the current user is the owner of the account they are trying to update
        if (!currentUser.getId().equals(user.getId()) && !(currentUser instanceof Admin)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You can only delete your own account or an admin can delete any account.");
        }

        Veterinarian existingVeterinarian = (Veterinarian) user;
        if (!existingVeterinarian.getTreatedPets().isEmpty()) {
            userRepository.removeAssociationVeterinarianWithPet(existingVeterinarian);
        }

        userRepository.deleteById(id);
    }

    public void deleteAdmin(Long id){
        String currentUsername = getCurrentUserName();
        if(currentUsername == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to delete an account.");
        }

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found."));

        // Check if the current user is an admin
        if (!(currentUser instanceof Admin)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only an admin can delete admin accounts.");
        }

        userRepository.deleteById(id);
    }

}
