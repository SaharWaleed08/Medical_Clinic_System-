package com.project.medical_clinic_system.securityTest;

import com.project.medical_clinic_system.enums.Role;
import com.project.medical_clinic_system.model.Admin;
import com.project.medical_clinic_system.model.Doctor;
import com.project.medical_clinic_system.model.User;
import com.project.medical_clinic_system.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private SecretKey key;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        key = (SecretKey) ReflectionTestUtils.getField(jwtService, "secretKey");
    }

    private Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    @Test
    void generateToken_returnsThreePartJwt() {
        String token = jwtService.generateToken(new Admin("Root", "root@mail.com", "pw", "01012345678"));

        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void generateToken_containsEmailAsSubjectAndRoleClaim() {
        User admin = new Admin("Root", "root@mail.com", "pw", "01012345678");

        Claims claims = parse(jwtService.generateToken(admin));

        assertEquals("root@mail.com", claims.getSubject());
        assertEquals(Role.ADMIN.name(), claims.get("role", String.class));
    }

    @Test
    void generateToken_roleClaimFollowsUserRole() {
        User doctor = new Doctor("Doc", "doc@mail.com", "pw", "01012345678", "1234-5678", 5, BigDecimal.TEN);

        Claims claims = parse(jwtService.generateToken(doctor));

        assertEquals(Role.DOCTOR.name(), claims.get("role", String.class));
    }

    @Test
    void generateToken_expiresInAboutOneHour() {
        Claims claims = parse(jwtService.generateToken(new Admin("Root", "root@mail.com", "pw", "01012345678")));

        long lifetime = claims.getExpiration().getTime() - claims.getIssuedAt().getTime();

        // JWT dates have second precision, so allow a 2s tolerance around 1 hour
        assertTrue(Math.abs(lifetime - 60 * 60 * 1000L) <= 2000, "lifetime was " + lifetime + "ms");
        assertTrue(claims.getExpiration().after(new Date()));
    }

    @Test
    void generateToken_isSignedWithServiceKey_otherKeyIsRejected() {
        String token = jwtService.generateToken(new Admin("Root", "root@mail.com", "pw", "01012345678"));
        // every JwtService instance generates its own random key
        SecretKey otherKey = (SecretKey) ReflectionTestUtils.getField(new JwtService(), "secretKey");

        assertThrows(SignatureException.class,
                () -> Jwts.parser().verifyWith(otherKey).build().parseSignedClaims(token));
    }
}
