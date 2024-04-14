package com.pethealth.finalproject.repository;

import com.pethealth.finalproject.model.Cat;
import com.pethealth.finalproject.model.Dog;
import com.pethealth.finalproject.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    Optional<Cat> findCatById(Long id);
    Optional<Dog> findDogById(Long id);
}
