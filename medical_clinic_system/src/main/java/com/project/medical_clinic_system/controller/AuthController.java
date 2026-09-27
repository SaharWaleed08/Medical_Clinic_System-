package com.project.medical_clinic_system.controller;

import com.project.medical_clinic_system.dto.request.LoginRequest;
import com.project.medical_clinic_system.dto.request.RegisterRequest;
import com.project.medical_clinic_system.dto.response.LoginResponse;
import com.project.medical_clinic_system.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/register")
    public Object register(@Valid @RequestBody RegisterRequest request){
        return authService.registerUser(request);
    }
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request){
        return authService.loginUser(request);
    }

}

