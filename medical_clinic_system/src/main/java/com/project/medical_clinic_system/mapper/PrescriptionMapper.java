package com.project.medical_clinic_system.mapper;

import com.project.medical_clinic_system.dto.response.DoctorResponse;
import com.project.medical_clinic_system.dto.response.PrescriptionResponse;
import com.project.medical_clinic_system.model.Doctor;
import com.project.medical_clinic_system.model.Prescription;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class PrescriptionMapper {
    public PrescriptionResponse toResponse(Optional<Prescription> prescription){
        return new PrescriptionResponse(prescription.get().getId(),prescription.get().getRecordID(),prescription.get().getMedicationName(),prescription.get().getDosage(),prescription.get().getFrequency(),prescription.get().getTreatmentDuration(),prescription.get().getAdditionalInstruction());
    }
    public List<PrescriptionResponse> toResponse(List<Prescription> prescriptions){
        List<PrescriptionResponse> responses = new ArrayList<>();

        for (Prescription prescription : prescriptions ) {
            responses.add(toResponse(Optional.ofNullable(prescription)));
        }

        return responses;
    }
}
