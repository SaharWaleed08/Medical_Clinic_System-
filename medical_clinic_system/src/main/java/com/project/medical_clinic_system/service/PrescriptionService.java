package com.project.medical_clinic_system.service;

import com.project.medical_clinic_system.dto.request.CreatePrescriptionRequest;
import com.project.medical_clinic_system.dto.response.PrescriptionResponse;
import com.project.medical_clinic_system.mapper.PrescriptionMapper;
import com.project.medical_clinic_system.model.Prescription;
import com.project.medical_clinic_system.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PrescriptionService {
    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionMapper prescriptionMapper;

    public PrescriptionService(PrescriptionRepository prescriptionRepository, PrescriptionMapper prescriptionMapper) {
        this.prescriptionRepository = prescriptionRepository;
        this.prescriptionMapper = prescriptionMapper;
    }

    public PrescriptionResponse createPrescription(CreatePrescriptionRequest request) {
        Prescription prescription = new Prescription(request.getRecordID(), request.getMedicationName(), request.getDosage(), request.getFrequency(), request.getTreatmentDuration(), request.getAdditionalInstruction());
        prescriptionRepository.save(prescription);
        return prescriptionMapper.toResponse(Optional.of(prescription));
    }

    public PrescriptionResponse findPrescriptionById(UUID prescriptionID) {
        Optional<Prescription> prescription = prescriptionRepository.findById(prescriptionID);
        return prescriptionMapper.toResponse(prescription);
    }
    public List<PrescriptionResponse> findAll(){
        List<Prescription> prescriptions=prescriptionRepository.findAll();
        return prescriptionMapper.toResponse(prescriptions);
    }
    public String deletePrescriptionById(UUID prescriptionID){
        prescriptionRepository.deleteById(prescriptionID);
        return "Prescription is deleted";
    }
}
