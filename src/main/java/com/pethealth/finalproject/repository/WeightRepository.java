package com.pethealth.finalproject.repository;

import com.pethealth.finalproject.model.HealthRecord;
import com.pethealth.finalproject.model.Weight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface WeightRepository extends JpaRepository<Weight, Long> {
    @Query("SELECT w FROM Weight w WHERE w.day BETWEEN :startDate AND :endDate")
    List<Weight> findAllBetweenDates(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT w FROM Weight w WHERE w.healthRecord = :healthRecord AND w.day BETWEEN :startDate AND :endDate")
    List<Weight> findAllByHealthRecordAndDayBetween(@Param("healthRecord") HealthRecord healthRecord, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
