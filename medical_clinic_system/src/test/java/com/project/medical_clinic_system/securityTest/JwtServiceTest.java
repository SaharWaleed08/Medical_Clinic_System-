package com.project.medical_clinic_system.securityTest;

import com.project.medical_clinic_system.enums.Role;
import com.project.medical_clinic_system.model.Admin;
import com.project.medical_clinic_system.model.Doctor;
import com.project.medical_clinic_system.model.Specialization;
import com.project.medical_clinic_system.model.User;
import com.project.medical_clinic_system.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final long ONE_HOUR = 3_600_000L;

    private static final String SECRET =
            Base64.getEncoder().encodeToString("0123456789abcdef0123456789abcdef".getBytes(StandardCharsets.UTF_8));
    private static final String OTHER_SECRET =
            Base64.getEncoder().encodeToString("ffffffffffffffffffffffffffffffff".getBytes(StandardCharsets.UTF_8));

    private JwtService jwtService;
    private User admin;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, ONE_HOUR);
        admin = new Admin("Root", "root@mail.com", "pw", "01012345678");
    }

    private SecretKey key(String base64) {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64));
    }

    // ---------- generateToken ----------

    @Test
    void generateToken_returnsThreePartJwt() {
        String token = jwtService.generateToken(admin, "session-1");

        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void generateToken_containsIssuerSubjectAndRole() {
        Claims claims = jwtService.extractClaims(jwtService.generateToken(admin, "session-1"));

        assertEquals("medical-clinic-system", claims.getIssuer());
        assertEquals("root@mail.com", claims.getSubject());
        assertEquals(Role.ADMIN.name(), claims.get("role", String.class));
    }

    @Test
    void generateToken_containsSessionIdClaim() {
        Claims claims = jwtService.extractClaims(jwtService.generateToken(admin, "session-xyz"));

        assertEquals("session-xyz", claims.get("sid", String.class));
    }

    @Test
    void generateToken_differentSessionIds_produceDifferentTokens() {
        String first = jwtService.generateToken(admin, "session-1");
        String second = jwtService.generateToken(admin, "session-2");

        assertNotEquals(first, second);
    }

    @Test
    void generateToken_roleClaimFollowsUserRole() {
        User doctor = new Doctor("Doc", "doc@mail.com", "pw", "01012345678",
                new Specialization("Cardiology", "Heart"), "1234-5678", 5, BigDecimal.TEN);

        Claims claims = jwtService.extractClaims(jwtService.generateToken(doctor, "session-1"));

        assertEquals(Role.DOCTOR.name(), claims.get("role", String.class));
    }

    @Test
    void generateToken_usesConfiguredExpiration() {
        JwtService shortLived = new JwtService(SECRET, 5 * 60 * 1000L);

        Claims claims = shortLived.extractClaims(shortLived.generateToken(admin, "session-1"));
        long lifetime = claims.getExpiration().getTime() - claims.getIssuedAt().getTime();

        // JWT dates have second precision -> allow 2s tolerance
        assertTrue(Math.abs(lifetime - 5 * 60 * 1000L) <= 2000, "lifetime was " + lifetime + "ms");
    }

    // ---------- isTokenValid ----------

    @Test
    void isTokenValid_freshToken_isTrue() {
        assertTrue(jwtService.isTokenValid(jwtService.generateToken(admin, "session-1")));
    }

    @Test
    void isTokenValid_garbage_isFalse() {
        assertFalse(jwtService.isTokenValid("not-a-jwt"));
    }

    @Test
    void isTokenValid_emptyString_isFalse() {
        assertFalse(jwtService.isTokenValid(""));
    }

    @Test
    void isTokenValid_tamperedPayload_isFalse() {
        String[] parts = jwtService.generateToken(admin, "session-1").split("\\.");
        String fakePayload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"sub\":\"hacker@mail.com\",\"role\":\"ADMIN\"}".getBytes(StandardCharsets.UTF_8));

        assertFalse(jwtService.isTokenValid(parts[0] + "." + fakePayload + "." + parts[2]));
    }

    @Test
    void isTokenValid_tokenSignedWithAnotherSecret_isFalse() {
        String foreignToken = new JwtService(OTHER_SECRET, ONE_HOUR).generateToken(admin, "session-1");

        assertFalse(jwtService.isTokenValid(foreignToken));
    }

    @Test
    void isTokenValid_expiredToken_isFalse() {
        JwtService alreadyExpired = new JwtService(SECRET, -1000L);

        assertFalse(jwtService.isTokenValid(alreadyExpired.generateToken(admin, "session-1")));
    }

    @Test
    void isTokenValid_wrongIssuer_isFalse() {
        String token = Jwts.builder()
                .issuer("some-other-app")
                .subject("root@mail.com")
                .claim("role", "ADMIN")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ONE_HOUR))
                .signWith(key(SECRET))
                .compact();

        assertFalse(jwtService.isTokenValid(token));
    }

    @Test
    void isTokenValid_missingIssuer_isFalse() {
        String token = Jwts.builder()
                .subject("root@mail.com")
                .claim("role", "ADMIN")
                .expiration(new Date(System.currentTimeMillis() + ONE_HOUR))
                .signWith(key(SECRET))
                .compact();

        assertFalse(jwtService.isTokenValid(token));
    }

    // ---------- tryExtractClaims ----------

    @Test
    void tryExtractClaims_validToken_returnsClaims() {
        Optional<Claims> claims = jwtService.tryExtractClaims(jwtService.generateToken(admin, "session-1"));

        assertTrue(claims.isPresent());
        assertEquals("root@mail.com", claims.get().getSubject());
    }

    @Test
    void tryExtractClaims_invalidToken_returnsEmpty() {
        assertTrue(jwtService.tryExtractClaims("garbage").isEmpty());
    }

    @Test
    void tryExtractClaims_expiredToken_returnsEmpty() {
        String expired = new JwtService(SECRET, -1000L).generateToken(admin, "session-1");

        assertTrue(jwtService.tryExtractClaims(expired).isEmpty());
    }

    // ---------- extractClaims ----------

    @Test
    void extractClaims_invalidToken_throws() {
        assertThrows(io.jsonwebtoken.JwtException.class, () -> jwtService.extractClaims("garbage"));
    }
}
