package com.project.medical_clinic_system.model;
import com.project.medical_clinic_system.enums.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "admins")
public class Admin extends User {

    public Admin() {
    }

    public Admin(String name, String email, String password, String phone) {
        super(name, email, password, phone, Role.ADMIN);
    }
}