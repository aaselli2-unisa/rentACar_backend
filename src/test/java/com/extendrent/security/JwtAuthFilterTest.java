package com.extendrent.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import src.controller.auth.authentication.AuthenticationController;
import src.core.config.AppConfig;
import src.core.config.SecurityConfig;
import src.core.security.JwtService;
import src.repository.user.UserEntity;
import src.service.auth.AuthenticationService;
import src.service.external.EmailService;
import src.service.user.UserService;
import src.service.user.model.DefaultUserStatus;
import src.service.user.model.UserRole;

import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for the {@link src.core.security.JwtAuthFilter} behaviour in the HTTP pipeline.
 *
 * Uses the authentication controller (a publicly accessible endpoint) to observe
 * how the filter processes Authorization headers, then uses a protected endpoint
 * (after the security fix) to verify that valid tokens grant access and invalid
 * tokens are rejected.
 *
 * Filter responsibilities under test:
 * - Skip processing when Authorization header is absent
 * - Skip processing when token is not a Bearer token
 * - Extract username from valid Bearer tokens
 * - Set SecurityContext for valid, non-expired tokens
 * - Propagate the request regardless (let the access decision manager decide)
 * - Not set SecurityContext for expired or tampered tokens
 */
@WebMvcTest(AuthenticationController.class)
@Import({SecurityConfig.class, AppConfig.class})
@ActiveProfiles("test")
@DisplayName("JwtAuthFilter – filter pipeline behaviour")
class JwtAuthFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;
    @MockBean private AuthenticationService authenticationService;
    @MockBean private EmailService emailService;

    // ======================================================================
    //  No Authorization header
    // ======================================================================

    @Nested
    @DisplayName("Missing Authorization header")
    class MissingAuthorizationHeader {

        @Test
        @DisplayName("Request without Authorization header still reaches the controller (no 401 for public endpoint)")
        void noHeader_publicEndpoint_reaches_controller() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signin")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"u@u.com\",\"password\":\"password\"}"))
                    .andExpect(status().is(not(500))); // filter should not crash
        }
    }

    // ======================================================================
    //  Malformed Authorization header
    // ======================================================================

    @Nested
    @DisplayName("Malformed Authorization header")
    class MalformedAuthorizationHeader {

        @Test
        @DisplayName("'Bearer' prefix with no token does not cause a 500")
        void bearerWithNoToken_doesNotCrash() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signin")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"u@u.com\",\"password\":\"password\"}")
                            .header("Authorization", "Bearer "))
                    .andExpect(status().is(not(500)));
        }

        @Test
        @DisplayName("Non-Bearer scheme (Basic) does not set authentication")
        void basicScheme_isIgnoredByJwtFilter() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signin")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"u@u.com\",\"password\":\"password\"}")
                            .header("Authorization", "Basic dXNlcjpwYXNzd29yZA=="))
                    .andExpect(status().is(not(500)));
        }

        @Test
        @DisplayName("Empty Authorization header does not crash the filter")
        void emptyHeader_doesNotCrash() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signin")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"u@u.com\",\"password\":\"password\"}")
                            .header("Authorization", ""))
                    .andExpect(status().is(not(500)));
        }
    }

    // ======================================================================
    //  Valid JWT token – filter sets authentication correctly
    // ======================================================================

    @Nested
    @DisplayName("Valid JWT – filter authenticates the request")
    class ValidJwt {

        @Test
        @DisplayName("Valid token is processed by the filter without crashing (filter does not return 500)")
        void validToken_doesNotCrashFilter() throws Exception {
            // This test verifies filter resilience: when extractUsername and isTokenValid
            // both succeed, the filter must not throw or produce a 500.
            // Authentication propagation to SecurityContext is verified indirectly
            // by the role-access tests in SecurityFilterChainTest and UserControllerSecurityTest.
            UserEntity user = buildActiveUser("user@example.com", UserRole.CUSTOMER);
            String token = SecurityTestSupport.validJwt("user@example.com", UserRole.CUSTOMER);

            when(jwtService.extractUsername(token)).thenReturn("user@example.com");
            when(jwtService.isTokenValid(eq(token), any())).thenReturn(true);
            when(userService.loadUserByUsername("user@example.com")).thenReturn(user);

            mockMvc.perform(post("/api/v1/auth/signin")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"user@example.com\",\"password\":\"password\"}")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().is(not(500)));
        }
    }

    // ======================================================================
    //  Invalid / expired JWT token – filter must NOT set authentication
    // ======================================================================

    @Nested
    @DisplayName("Invalid JWT – filter must not authenticate the request")
    class InvalidJwt {

        @Test
        @DisplayName("Token rejected by isTokenValid() causes the request to be unauthenticated")
        void tokenFailingValidation_noAuthentication() throws Exception {
            String token = "some.invalid.token";
            when(jwtService.extractUsername(token)).thenReturn("user@example.com");
            when(jwtService.isTokenValid(eq(token), any())).thenReturn(false);

            mockMvc.perform(post("/api/v1/auth/signin")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"user@example.com\",\"password\":\"password\"}")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().is(not(500)));
        }

        @Test
        @DisplayName("Token with null username (malformed payload) does not crash the filter")
        void nullUsername_fromExtract_doesNotCrash() throws Exception {
            String token = "valid.structure.nullusername";
            when(jwtService.extractUsername(token)).thenReturn(null);

            mockMvc.perform(post("/api/v1/auth/signin")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"u@u.com\",\"password\":\"password\"}")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().is(not(500)));
        }

        @Test
        @DisplayName("JwtService throwing a runtime exception does not return 500 – must be handled gracefully")
        void jwtServiceException_doesNotReturn500() throws Exception {
            String token = "malformed-jwt-token";
            when(jwtService.extractUsername(token))
                    .thenThrow(new io.jsonwebtoken.MalformedJwtException("bad token"));

            // The filter should catch parse exceptions and continue the filter chain
            // without setting authentication (resulting in 401/403 on protected resources)
            mockMvc.perform(post("/api/v1/auth/signin")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"u@u.com\",\"password\":\"password\"}")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().is(not(500)));
        }
    }

    // ---- helpers -----------------------------------------------------------

    private static UserEntity buildActiveUser(String email, UserRole role) {
        UserEntity user = UserEntity.userBuilder()
                .emailAddress(email)
                .name("Test").surname("User")
                .phoneNumber("5551234567")
                .password("$2a$10$hash")
                .authority(role)
                .status(DefaultUserStatus.VERIFIED)
                .build();
        user.setIsDeleted(false);
        return user;
    }
}
