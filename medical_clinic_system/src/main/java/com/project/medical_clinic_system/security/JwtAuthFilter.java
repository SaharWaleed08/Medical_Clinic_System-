package com.project.medical_clinic_system.security;

import com.project.medical_clinic_system.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith("Bearer ")
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            jwtService.tryExtractClaims(header.substring(7)).ifPresent(claims -> {
                String email = claims.getSubject();
                String tokenRole = claims.get("role", String.class);

                if (email != null && tokenRole != null) {
                    userRepository.findByEmail(email)
                            .filter(u -> u.getRole() != null
                                    && u.getRole().name().equals(tokenRole))
                            .ifPresent(user -> {
                                var authentication = new UsernamePasswordAuthenticationToken(
                                        user,
                                        null,
                                        List.of(new SimpleGrantedAuthority(
                                                "ROLE_" + user.getRole().name())));
                                SecurityContextHolder.getContext().setAuthentication(authentication);
                            });
                }
            });
        }

        chain.doFilter(request, response);
    }
}