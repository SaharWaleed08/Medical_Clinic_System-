package com.project.medical_clinic_system.exception;

public class InvalidStatusTransition extends RuntimeException{
    public InvalidStatusTransition(String message){
        super(message);
    }
}
