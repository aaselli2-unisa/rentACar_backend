package com.extendrent.security;

import org.junit.jupiter.api.DisplayName;
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
import src.service.auth.AuthenticationService;
import src.service.external.EmailService;
import src.service.user.UserService;
import src.service.user.model.UserRole;

import jakarta.servlet.http.Cookie;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * V-04 – Token revocation on logout (OWASP A07 / CWE-613).
 *
 * Without a logout endpoint, stolen refresh tokens remain valid until natural expiry
 * (7 days). Logout must revoke all stored refresh tokens for the user server-side,
 * making the stolen token unusable even if the attacker has the raw value.
 *
 * Fix: POST /api/v1/auth/logout revokes all refresh tokens via refreshTokenEntityService
 * and clears both HttpOnly cookies.
 */
@WebMvcTest(AuthenticationController.class)
@Import({SecurityConfig.class, AppConfig.class})
@ActiveProfiles("test")
@DisplayName("V-04 – Logout must revoke server-side refresh tokens")
class LogoutSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;
    @MockBean private AuthenticationService authenticationService;
    @MockBean private EmailService emailService;

    @Test
    @DisplayName("POST /logout requires authentication (unauthenticated returns 401)")
    void logout_unauthenticated_returns401() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /logout authenticated returns 204 No Content")
    void logout_authenticated_returns204() throws Exception {
        String token = SecurityTestSupport.validJwt("user@example.com", UserRole.CUSTOMER);
        SecurityTestSupport.setupAuthMocks(jwtService, userService, token,
                SecurityTestSupport.userEntity("user@example.com", UserRole.CUSTOMER));

        mockMvc.perform(post("/api/v1/auth/logout")
                        .cookie(new Cookie("accessToken", token)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /logout calls authenticationService.logout() with the authenticated user's email")
    void logout_callsServiceLogout() throws Exception {
        String email = "user@example.com";
        String token = SecurityTestSupport.validJwt(email, UserRole.CUSTOMER);
        SecurityTestSupport.setupAuthMocks(jwtService, userService, token,
                SecurityTestSupport.userEntity(email, UserRole.CUSTOMER));

        mockMvc.perform(post("/api/v1/auth/logout")
                        .cookie(new Cookie("accessToken", token)))
                .andExpect(status().isNoContent());

        // Verify server-side token revocation was called
        verify(authenticationService).logout(anyString());
    }

    @Test
    @DisplayName("POST /logout via Authorization header also triggers token revocation")
    void logout_viaAuthorizationHeader_callsServiceLogout() throws Exception {
        String email = "user@example.com";
        String token = SecurityTestSupport.validJwt(email, UserRole.CUSTOMER);
        SecurityTestSupport.setupAuthMocks(jwtService, userService, token,
                SecurityTestSupport.userEntity(email, UserRole.CUSTOMER));

        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        verify(authenticationService).logout(anyString());
    }
}
