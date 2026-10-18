package com.project.medical_clinic_system.config;

import com.project.medical_clinic_system.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Public
                        .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()

                        // Admin (AdminController + AdminGetController)
                        .requestMatchers("/api/admin/**", "/api/admins/**").hasRole("ADMIN")

                        // Doctor (DoctorController): create/delete = ADMIN, update handled by @PreAuthorize
                        .requestMatchers(HttpMethod.POST, "/api/doctor").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/doctor/**").hasRole("ADMIN")

                        // Specialization (SpecializationController): writes = ADMIN
                        .requestMatchers("/api/specialization/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/specializations").permitAll()

                        // Medical records & prescriptions (writes = DOCTOR)
                        .requestMatchers("/api/medical-record/**", "/api/prescription/**").hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.GET,"/api/medical-record/**", "/api/prescription/**").hasAnyRole("DOCTOR","PATIENT")

                        // Availability (writes = DOCTOR or ADMIN, ownership checked by @PreAuthorize)
                        .requestMatchers("/api/availability/**").hasAnyRole("DOCTOR", "ADMIN")

                        // Appointments (AppointmentController)
                        .requestMatchers(HttpMethod.POST, "/api/appointment").hasRole("PATIENT")
                        .requestMatchers(HttpMethod.PATCH,
                                "/api/appointment/*/confirm",
                                "/api/appointment/*/complete",
                                "/api/appointment/*/no-show").hasAnyRole("DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/appointment/*/cancel")
                        .hasAnyRole("PATIENT", "DOCTOR", "ADMIN")

                        // Appointments listing (AppointmentGetController) = ADMIN
                        .requestMatchers("/api/appointments/**").hasRole("ADMIN")

                        // Patient (PatientController): create = ADMIN, update/delete = ADMIN or self (@PreAuthorize)
                        .requestMatchers("/api/patient/**").hasAnyRole("PATIENT", "ADMIN")

                        // Read endpoints where @PreAuthorize decides per role / ownership:
                        // /api/patients/**, /api/doctors/**, /api/specializations/**,
                        // /api/availabilities/**, /api/medical-records/**, /api/prescriptions/**
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}