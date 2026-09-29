package com.project.medical_clinic_system.mapper;


import com.project.medical_clinic_system.dto.response.PrescriptionResponse;
import com.project.medical_clinic_system.model.Prescription;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PrescriptionMapper {
    public PrescriptionResponse toResponse(Prescription prescription){
        return new PrescriptionResponse(
                prescription.getId(),
                prescription.getRecordID(),
                prescription.getMedicationName(),
                prescription.getDosage(),
                prescription.getFrequency(),
                prescription.getTreatmentDuration(),
                prescription.getAdditionalInstruction());
    }
    public List<PrescriptionResponse> toResponse(List<Prescription> prescriptions){
        List<PrescriptionResponse> responses = new ArrayList<>();

        for (Prescription prescription : prescriptions ) {
            responses.add(toResponse(prescription));
        }

        return responses;
    }
}
