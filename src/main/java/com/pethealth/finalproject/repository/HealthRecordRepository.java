package com.pethealth.finalproject.repository;

import com.pethealth.finalproject.model.HealthRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {

    //usar solo cuando se necesite el array de weights
    @Query("SELECT h FROM HealthRecord h JOIN FETCH h.weights WHERE h.id = :id")
    Optional<HealthRecord> findByIdAndInitializeWeights(@Param("id") Long id);

    @Query("SELECT h FROM HealthRecord h JOIN FETCH h.events WHERE h.id = :id")
    Optional<HealthRecord> findByIdAndInitializeEvents(@Param("id") Long id);
}
