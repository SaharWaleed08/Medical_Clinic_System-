package com.project.medical_clinic_system.repository;

import com.project.medical_clinic_system.model.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, UUID> {

    List<Availability> findByDoctorDoctorIDAndDay(UUID doctorID, DayOfWeek day);
}