package com.project.medical_clinic_system.exception;

public class DoctorUnavailableException extends RuntimeException{
    public DoctorUnavailableException(String message){
        super(message);
    }
}
