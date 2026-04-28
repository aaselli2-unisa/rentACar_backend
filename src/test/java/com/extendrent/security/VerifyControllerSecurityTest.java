package com.extendrent.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import src.controller.auth.verify.VerifyController;
import src.core.config.AppConfig;
import src.core.config.SecurityConfig;
import src.core.security.JwtService;
import src.service.auth.VerifyService;
import src.service.user.UserService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security tests for {@link VerifyController}.
 *
 * GET /api/v1/verify/email is a PUBLIC endpoint (permitAll) used in the
 * email-verification link sent after signup. The user clicks the link
 * without being logged in yet — authentication cannot be required.
 *
 * Security risk: NONE (no authorization logic to bypass — the endpoint
 * validates a one-time token, not a role). These tests guard against
 * a regression where someone accidentally adds authenticated() to
 * /api/v1/verify/**, which would silently break the signup flow.
 *
 * Closes gap G6 from ENDPOINT_ACCESS_MATRIX.md.
 */
@WebMvcTest(VerifyController.class)
@Import({SecurityConfig.class, AppConfig.class})
@ActiveProfiles("test")
@DisplayName("VerifyController – security tests (gap G6, public endpoint)")
class VerifyControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;
    @MockBean private VerifyService verifyService;

    // ======================================================================
    //  Public access — GET /api/v1/verify/email must NEVER require auth
    //  (user clicks the link in their inbox before having a session)
    // ======================================================================

    @Nested
    @DisplayName("Public access – no token required")
    class PublicAccess {

        @Test
        @DisplayName("GET /api/v1/verify/email returns 200 without any Authorization header")
        void verifyEmail_noAuth_returns200() throws Exception {
            when(verifyService.verifyEmailAddress(anyString())).thenReturn(null);
            mockMvc.perform(get("/api/v1/verify/email").param("token", "sometoken"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET /api/v1/verify/email returns 200 for CUSTOMER role (already logged in)")
        @WithMockUser(roles = "CUSTOMER")
        void verifyEmail_customerRole_returns200() throws Exception {
            when(verifyService.verifyEmailAddress(anyString())).thenReturn(null);
            mockMvc.perform(get("/api/v1/verify/email").param("token", "sometoken"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET /api/v1/verify/email does NOT return 401 (regression guard)")
        void verifyEmail_noAuth_notUnauthorized() throws Exception {
            when(verifyService.verifyEmailAddress(anyString())).thenReturn(null);
            mockMvc.perform(get("/api/v1/verify/email").param("token", "abc"))
                    .andExpect(result ->
                            org.assertj.core.api.Assertions.assertThat(
                                    result.getResponse().getStatus()).isNotEqualTo(401)
                    );
        }
    }
}
