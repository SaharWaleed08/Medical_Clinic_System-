package com.project.medical_clinic_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain
    securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(
                        auth
                                -> auth.requestMatchers("/api/auth/register")
                                .permitAll()
                                .requestMatchers("/api/auth/login")
                                .permitAll()
                                .requestMatchers("/auth/user/**")
                                .hasRole("USER")
                                .requestMatchers("/auth/admin/**")
                                .hasRole("ADMIN")
                                .anyRequest()
                                .authenticated());


        return http.build();
    }
}
