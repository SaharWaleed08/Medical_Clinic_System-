package com.project.medical_clinic_system.mapper;

import com.project.medical_clinic_system.dto.response.DoctorResponse;
import com.project.medical_clinic_system.dto.response.PatientResponse;
import com.project.medical_clinic_system.model.Doctor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DoctorMapper {
    public DoctorResponse toResponse(Optional<Doctor> doctor) {
        return new DoctorResponse(doctor.get().getDoctorID(), doctor.get().getName(),doctor.get().getEmail());
    }

    public List<DoctorResponse> toResponse(List<Doctor> doctors) {
        List<DoctorResponse> responses = new ArrayList<>();

        for (Doctor doctor : doctors ) {
            responses.add(toResponse(Optional.ofNullable(doctor)));
        }

        return responses;
    }
}
