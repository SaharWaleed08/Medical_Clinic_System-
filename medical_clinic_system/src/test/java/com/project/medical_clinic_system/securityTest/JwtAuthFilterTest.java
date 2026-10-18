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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

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
        admin = new Admin("Root", "root@mail.com", "pw", "01012345678");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void assertChainContinued() {
        assertNotNull(chain.getRequest(), "filter must always continue the chain");
    }

    private void stubValidClaims(String email, String role) {
        when(jwtService.tryExtractClaims("good-token")).thenReturn(Optional.of(claims));
        when(claims.getSubject()).thenReturn(email);
        when(claims.get("role", String.class)).thenReturn(role);
    }

    @Test
    void noAuthorizationHeader_doesNotAuthenticate_andContinuesChain() throws Exception {
        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertChainContinued();
        verifyNoInteractions(jwtService, userRepository);
    }

    @Test
    void headerWithoutBearerPrefix_isIgnored() throws Exception {
        request.addHeader("Authorization", "Basic abc123");

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertChainContinued();
        verifyNoInteractions(jwtService, userRepository);
    }

    @Test
    void validToken_authenticatesUserWithRoleAuthority() throws Exception {
        request.addHeader("Authorization", "Bearer good-token");
        stubValidClaims("root@mail.com", "ADMIN");
        when(userRepository.findByEmail("root@mail.com")).thenReturn(Optional.of(admin));

        filter.doFilter(request, response, chain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertSame(admin, auth.getPrincipal());
        assertNull(auth.getCredentials());
        assertEquals(1, auth.getAuthorities().size());
        assertEquals("ROLE_ADMIN", auth.getAuthorities().iterator().next().getAuthority());
        assertChainContinued();
    }

    @Test
    void invalidToken_doesNotAuthenticate_butContinuesChain() throws Exception {
        request.addHeader("Authorization", "Bearer bad-token");
        when(jwtService.tryExtractClaims("bad-token")).thenReturn(Optional.empty());

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertChainContinued();
        verifyNoInteractions(userRepository);
    }

    @Test
    void tokenRoleDifferentFromCurrentUserRole_doesNotAuthenticate() throws Exception {
        // e.g. token was issued while the user was a DOCTOR but the account is ADMIN now
        request.addHeader("Authorization", "Bearer good-token");
        stubValidClaims("root@mail.com", "DOCTOR");
        when(userRepository.findByEmail("root@mail.com")).thenReturn(Optional.of(admin));

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertChainContinued();
    }

    @Test
    void tokenForUnknownUser_doesNotAuthenticate() throws Exception {
        request.addHeader("Authorization", "Bearer good-token");
        stubValidClaims("ghost@mail.com", "ADMIN");
        when(userRepository.findByEmail("ghost@mail.com")).thenReturn(Optional.empty());

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertChainContinued();
    }

    @Test
    void tokenWithoutRoleClaim_doesNotAuthenticate_andDoesNotHitDatabase() throws Exception {
        request.addHeader("Authorization", "Bearer good-token");
        stubValidClaims("root@mail.com", null);

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertChainContinued();
        verifyNoInteractions(userRepository);
    }

    @Test
    void tokenWithoutSubject_doesNotAuthenticate_andDoesNotHitDatabase() throws Exception {
        request.addHeader("Authorization", "Bearer good-token");
        when(jwtService.tryExtractClaims("good-token")).thenReturn(Optional.of(claims));
        when(claims.getSubject()).thenReturn(null);
        when(claims.get("role", String.class)).thenReturn("ADMIN");

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertChainContinued();
        verifyNoInteractions(userRepository);
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
}
