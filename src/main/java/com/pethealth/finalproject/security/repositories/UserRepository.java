package com.pethealth.finalproject.security.repositories;

import com.pethealth.finalproject.model.Owner;
import com.pethealth.finalproject.model.Veterinarian;
import com.pethealth.finalproject.security.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * The UserRepository interface extends JpaRepository to allow for CRUD operations
 * on User entities in the database.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Method to find a User entity by its username field
     *
     * @param username The username of the User entity to search for
     * @return The found User entity or null if not found
     */
    User findByUsername(String username);

//    @Modifying
//    @Query("UPDATE Patient p SET p.employee = null WHERE p.employee = :employee")
//    void removeAssociationWithEmployee(@Param("employee") Employee employee);

    @Modifying
    @Query("UPDATE Pet p SET p.veterinarian = null WHERE p.veterinarian = :veterinarian")
    void removeAssociationVeterinarianWithPet(@Param("veterinarian") Veterinarian veterinarian);

}
