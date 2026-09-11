package com.project.medical_clinic_system.repository;

import com.project.medical_clinic_system.model.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.UUID;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, UUID> {
    void deleteById(UUID doctorID, DayOfWeek day);
}
