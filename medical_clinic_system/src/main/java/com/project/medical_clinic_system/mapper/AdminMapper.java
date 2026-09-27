package com.project.medical_clinic_system.mapper;

import com.project.medical_clinic_system.dto.response.AdminResponse;
import com.project.medical_clinic_system.model.Admin;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class AdminMapper {
    public AdminResponse toResponse(Admin admin){
        return new AdminResponse(admin.getId(), admin.getName(),admin.getEmail());
    }
    public List<AdminResponse> toResponse(List<Admin> admins) {
        List<AdminResponse> responses = new ArrayList<>();

        for (Admin admin : admins) {
            responses.add(toResponse(admin));
        }

        return responses;
    }
}
