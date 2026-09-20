package com.project.medical_clinic_system.exception;

public class AppointmentConflict extends RuntimeException{
    public AppointmentConflict(String message){
        super(message);
    }
}
