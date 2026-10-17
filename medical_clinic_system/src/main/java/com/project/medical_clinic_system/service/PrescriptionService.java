package com.project.medical_clinic_system.service;

import com.project.medical_clinic_system.dto.request.CreatePrescriptionRequest;
import com.project.medical_clinic_system.dto.response.PrescriptionResponse;
import com.project.medical_clinic_system.exception.ResourceNotFoundException;
import com.project.medical_clinic_system.mapper.PrescriptionMapper;
import com.project.medical_clinic_system.model.MedicalVisitRecord;
import com.project.medical_clinic_system.model.Prescription;
import com.project.medical_clinic_system.repository.MedicalRecordRepository;
import com.project.medical_clinic_system.repository.PrescriptionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PrescriptionService {
    private final PrescriptionRepository prescriptionRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final PrescriptionMapper prescriptionMapper;

    public PrescriptionService(PrescriptionRepository prescriptionRepository, MedicalRecordRepository medicalRecordRepository, PrescriptionMapper prescriptionMapper) {
        this.prescriptionRepository = prescriptionRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.prescriptionMapper = prescriptionMapper;
    }

    public PrescriptionResponse createPrescription(CreatePrescriptionRequest request) {
        MedicalVisitRecord medicalVisitRecord=medicalRecordRepository.findById(request.getRecordID())
                .orElseThrow(()->new ResourceNotFoundException("Medical record not found"));
        Prescription prescription = new Prescription(medicalVisitRecord, request.getMedicationName(), request.getDosage(), request.getFrequency(), request.getTreatmentDuration(), request.getAdditionalInstruction());
        prescriptionRepository.save(prescription);
        return prescriptionMapper.toResponse(prescription);
    }

    public PrescriptionResponse findPrescriptionById(UUID prescriptionID) {
        Prescription prescription = prescriptionRepository.findById(prescriptionID)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found"));
        return prescriptionMapper.toResponse(prescription);
    }

    public List<PrescriptionResponse> findAll() {
        List<Prescription> prescriptions = prescriptionRepository.findAll();
        return prescriptionMapper.toResponse(prescriptions);
    }

    public String deletePrescriptionById(UUID prescriptionID) {
        prescriptionRepository.deleteById(prescriptionID);
        return "Prescription is deleted";
    }
}
