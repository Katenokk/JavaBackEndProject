package com.pethealth.finalproject.repository;

import com.pethealth.finalproject.model.Cat;
import com.pethealth.finalproject.model.Dog;
import com.pethealth.finalproject.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    Optional<Cat> findCatById(Long id);
    Optional<Dog> findDogById(Long id);

    Optional<Pet> findByName(String name);

    @Query("SELECT p FROM Pet p LEFT JOIN FETCH p.healthRecord h LEFT JOIN FETCH h.weights WHERE p.id = :id")
    Optional<Pet> findByIdAndFetchWeightsEagerly(@Param("id") Long id);

    //para poder inicializar los eventos para añadir eventos a pet
    @Query("SELECT p FROM Pet p LEFT JOIN FETCH p.healthRecord h LEFT JOIN FETCH h.events WHERE p.id = :id")
    Optional<Pet> findByIdAndFetchEventsEagerly(@Param("id") Long id);
}
