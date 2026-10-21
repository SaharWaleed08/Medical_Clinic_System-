package com.project.medical_clinic_system.securityTest;

import com.project.medical_clinic_system.model.Admin;
import com.project.medical_clinic_system.model.User;
import com.project.medical_clinic_system.repository.UserRepository;
import com.project.medical_clinic_system.security.JwtAuthFilter;
import com.project.medical_clinic_system.security.JwtService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    private static final String SESSION_ID = "session-123";

    @Mock
    private JwtService jwtService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private Claims claims;

    private JwtAuthFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain chain;

    private User admin;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        filter = new JwtAuthFilter(jwtService, userRepository);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();

        // an admin with a currently valid session
        admin = new Admin("Root", "root@mail.com", "pw", "01012345678");
        admin.setActiveSessionId(SESSION_ID);
        admin.setActiveSessionExpiresAt(Instant.now().plusSeconds(600));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void assertChainContinued() {
        assertNotNull(chain.getRequest(), "filter must always continue the chain");
    }

    private void assertNotAuthenticated() {
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    /** Stubs the three claims the filter always reads: subject, role and sid. */
    private void stubValidClaims(String email, String role, String sid) {
        when(jwtService.tryExtractClaims("good-token")).thenReturn(Optional.of(claims));
        when(claims.getSubject()).thenReturn(email);
        when(claims.get("role", String.class)).thenReturn(role);
        when(claims.get("sid", String.class)).thenReturn(sid);
    }

    private void sendGoodToken() throws Exception {
        request.addHeader("Authorization", "Bearer good-token");
        filter.doFilter(request, response, chain);
    }

    // ---------- header handling ----------

    @Test
    void noAuthorizationHeader_doesNotAuthenticate_andContinuesChain() throws Exception {
        filter.doFilter(request, response, chain);

        assertNotAuthenticated();
        assertChainContinued();
        verifyNoInteractions(jwtService, userRepository);
    }

    @Test
    void headerWithoutBearerPrefix_isIgnored() throws Exception {
        request.addHeader("Authorization", "Basic abc123");

        filter.doFilter(request, response, chain);

        assertNotAuthenticated();
        assertChainContinued();
        verifyNoInteractions(jwtService, userRepository);
    }

    @Test
    void alreadyAuthenticatedRequest_isNotOverwritten() throws Exception {
        Authentication existing = new UsernamePasswordAuthenticationToken("someone", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(existing);

        request.addHeader("Authorization", "Bearer good-token");
        filter.doFilter(request, response, chain);

        assertSame(existing, SecurityContextHolder.getContext().getAuthentication());
        assertChainContinued();
        verify(jwtService, never()).tryExtractClaims(any());
    }

    // ---------- happy path ----------

    @Test
    void validTokenAndActiveSession_authenticatesUserWithRoleAuthority() throws Exception {
        stubValidClaims("root@mail.com", "ADMIN", SESSION_ID);
        when(userRepository.findByEmail("root@mail.com")).thenReturn(Optional.of(admin));

        sendGoodToken();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertSame(admin, auth.getPrincipal());
        assertNull(auth.getCredentials());
        assertEquals(1, auth.getAuthorities().size());
        assertEquals("ROLE_ADMIN", auth.getAuthorities().iterator().next().getAuthority());
        assertChainContinued();
    }

    // ---------- invalid token / missing claims ----------

    @Test
    void invalidToken_doesNotAuthenticate_butContinuesChain() throws Exception {
        request.addHeader("Authorization", "Bearer bad-token");
        when(jwtService.tryExtractClaims("bad-token")).thenReturn(Optional.empty());

        filter.doFilter(request, response, chain);

        assertNotAuthenticated();
        assertChainContinued();
        verifyNoInteractions(userRepository);
    }

    @Test
    void tokenWithoutSubject_doesNotAuthenticate_andDoesNotHitDatabase() throws Exception {
        stubValidClaims(null, "ADMIN", SESSION_ID);

        sendGoodToken();

        assertNotAuthenticated();
        assertChainContinued();
        verifyNoInteractions(userRepository);
    }

    @Test
    void tokenWithoutRoleClaim_doesNotAuthenticate_andDoesNotHitDatabase() throws Exception {
        stubValidClaims("root@mail.com", null, SESSION_ID);

        sendGoodToken();

        assertNotAuthenticated();
        assertChainContinued();
        verifyNoInteractions(userRepository);
    }

    @Test
    void tokenWithoutSessionIdClaim_doesNotAuthenticate_andDoesNotHitDatabase() throws Exception {
        // old tokens issued before single-session login have no "sid" and must be rejected
        stubValidClaims("root@mail.com", "ADMIN", null);

        sendGoodToken();

        assertNotAuthenticated();
        assertChainContinued();
        verifyNoInteractions(userRepository);
    }

    // ---------- user checks ----------

    @Test
    void tokenForUnknownUser_doesNotAuthenticate() throws Exception {
        stubValidClaims("ghost@mail.com", "ADMIN", SESSION_ID);
        when(userRepository.findByEmail("ghost@mail.com")).thenReturn(Optional.empty());

        sendGoodToken();

        assertNotAuthenticated();
        assertChainContinued();
    }

    @Test
    void tokenRoleDifferentFromCurrentUserRole_doesNotAuthenticate() throws Exception {
        stubValidClaims("root@mail.com", "DOCTOR", SESSION_ID);
        when(userRepository.findByEmail("root@mail.com")).thenReturn(Optional.of(admin));

        sendGoodToken();

        assertNotAuthenticated();
        assertChainContinued();
    }

    // ---------- single-session checks ----------

    @Test
    void sessionIdDifferentFromStoredOne_doesNotAuthenticate() throws Exception {
        // e.g. the user logged in again elsewhere, so this older token is no longer valid
        stubValidClaims("root@mail.com", "ADMIN", "some-older-session");
        when(userRepository.findByEmail("root@mail.com")).thenReturn(Optional.of(admin));

        sendGoodToken();

        assertNotAuthenticated();
        assertChainContinued();
    }

    @Test
    void afterLogout_tokenIsRejected() throws Exception {
        admin.setActiveSessionId(null);
        admin.setActiveSessionExpiresAt(null);
        stubValidClaims("root@mail.com", "ADMIN", SESSION_ID);
        when(userRepository.findByEmail("root@mail.com")).thenReturn(Optional.of(admin));

        sendGoodToken();

        assertNotAuthenticated();
        assertChainContinued();
    }

    @Test
    void storedSessionExpired_doesNotAuthenticate() throws Exception {
        admin.setActiveSessionExpiresAt(Instant.now().minusSeconds(1));
        stubValidClaims("root@mail.com", "ADMIN", SESSION_ID);
        when(userRepository.findByEmail("root@mail.com")).thenReturn(Optional.of(admin));

        sendGoodToken();

        assertNotAuthenticated();
        assertChainContinued();
    }

    @Test
    void storedSessionWithoutExpiry_doesNotAuthenticate() throws Exception {
        admin.setActiveSessionExpiresAt(null);
        stubValidClaims("root@mail.com", "ADMIN", SESSION_ID);
        when(userRepository.findByEmail("root@mail.com")).thenReturn(Optional.of(admin));

        sendGoodToken();

        assertNotAuthenticated();
        assertChainContinued();
    }
}
