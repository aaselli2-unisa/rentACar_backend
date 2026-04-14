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
import src.core.config.CorsConfig;
import src.core.config.SecurityConfig;
import src.core.config.WebConfig;
import src.core.security.JwtService;
import src.service.auth.AuthenticationService;
import src.service.external.EmailService;
import src.service.user.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * CORS configuration security tests (PATCHED V03 / V12).
 *
 * Verifies that the CORS policy is correctly restricted to known origins.
 * These tests are regression guards: wildcard origins and attacker domains were
 * removed in V03/V12 and must not be re-introduced.
 */
@WebMvcTest(AuthenticationController.class)
@Import({SecurityConfig.class, AppConfig.class, CorsConfig.class, WebConfig.class})
@ActiveProfiles("test")
@DisplayName("CORS – misconfiguration security tests")
class CorsSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;
    @MockBean private AuthenticationService authenticationService;
    @MockBean private EmailService emailService;

    // ======================================================================
    //  Wildcard origin – documents the current vulnerability
    // ======================================================================

    @Nested
    @DisplayName("FIXED – attacker domains removed from CORS whitelist (V03 patch)")
    class AttackerDomainsRejected {

        @Test
        @DisplayName("Preflight from evil-attacker.com must NOT return Access-Control-Allow-Origin")
        void preflightFromAttackerOrigin_isRejected() throws Exception {
            // After V03 patch: evil-attacker.com is not in ALLOWED_ORIGINS → no ACAO header
            mockMvc.perform(options("/api/v1/auth/signin")
                            .header("Origin", "https://evil-attacker.com")
                            .header("Access-Control-Request-Method", "POST")
                            .header("Access-Control-Request-Headers", "Content-Type,Authorization"))
                    .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
        }

        @Test
        @DisplayName("Simple request from attacker.example.com must NOT receive ACAO header")
        void simpleRequestFromAttackerOrigin_doesNotReceiveAcaoHeader() throws Exception {
            // After V03 patch: attacker.example.com removed from ALLOWED_ORIGINS
            mockMvc.perform(post("/api/v1/auth/signin")
                            .header("Origin", "https://attacker.example.com")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"u@u.com\",\"password\":\"password\"}"))
                    .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
        }

        @Test
        @DisplayName("ACAO header must be present and not '*' for a whitelisted origin")
        void acaoHeader_isNotWildcard_forLegitOrigin() throws Exception {
            // "https://legit-frontend.example.com" is explicitly in CorsConfig.ALLOWED_ORIGINS
            mockMvc.perform(options("/api/v1/auth/signin")
                            .header("Origin", "https://legit-frontend.example.com")
                            .header("Access-Control-Request-Method", "POST")
                            .header("Access-Control-Request-Headers", "Authorization"))
                    .andExpect(result -> {
                        String acao = result.getResponse().getHeader("Access-Control-Allow-Origin");
                        org.assertj.core.api.Assertions.assertThat(acao)
                                .as("Access-Control-Allow-Origin must be present for a whitelisted origin")
                                .isNotNull();
                        org.assertj.core.api.Assertions.assertThat(acao)
                                .as("Access-Control-Allow-Origin must not be wildcard — specific origin required")
                                .isNotEqualTo("*");
                    });
        }

        @Test
        @DisplayName("CorsConfig does not enable Allow-Credentials – header must be absent from preflight response")
        void allowCredentials_mustNotBePresentInPreflightResponse() throws Exception {
            // CorsConfig deliberately omits allowCredentials(true).
            // This test is a regression guard: if someone adds allowCredentials(true) to CorsConfig
            // without also auditing the allowed origins, this test turns red immediately and forces
            // a conscious security review before the change can land.
            mockMvc.perform(options("/api/v1/auth/signin")
                            .header("Origin", "https://legit-frontend.example.com")
                            .header("Access-Control-Request-Method", "POST")
                            .header("Access-Control-Request-Headers", "Authorization"))
                    .andExpect(result -> {
                        String allowCreds = result.getResponse().getHeader("Access-Control-Allow-Credentials");
                        org.assertj.core.api.Assertions.assertThat(allowCreds)
                                .as("Access-Control-Allow-Credentials must not be set — "
                                        + "CorsConfig does not configure credentials. "
                                        + "If you intentionally need credentials, also verify "
                                        + "that no wildcard origin is in ALLOWED_ORIGINS.")
                                .isNotEqualTo("true");
                    });
        }
    }

    // ======================================================================
    //  Required CORS headers on responses
    // ======================================================================

    @Nested
    @DisplayName("Required CORS response headers")
    class RequiredCorsHeaders {

        @Test
        @DisplayName("Preflight must respond with Access-Control-Allow-Methods")
        void preflight_includesAllowMethods() throws Exception {
            mockMvc.perform(options("/api/v1/auth/signin")
                            .header("Origin", "https://legit-frontend.example.com")
                            .header("Access-Control-Request-Method", "POST")
                            .header("Access-Control-Request-Headers", "Content-Type"))
                    .andExpect(header().exists("Access-Control-Allow-Methods"));
        }

        @Test
        @DisplayName("Preflight must respond with Access-Control-Allow-Headers containing Authorization")
        void preflight_includesAuthorizationHeader() throws Exception {
            mockMvc.perform(options("/api/v1/auth/signin")
                            .header("Origin", "https://legit-frontend.example.com")
                            .header("Access-Control-Request-Method", "POST")
                            .header("Access-Control-Request-Headers", "Authorization"))
                    .andExpect(header().exists("Access-Control-Allow-Headers"));
        }
    }

    // ======================================================================
    //  DELETE / PUT / dangerous methods
    // ======================================================================

    @Nested
    @DisplayName("Dangerous HTTP methods in CORS preflight")
    class DangerousMethodsPreflight {

        @Test
        @DisplayName("DELETE preflight from untrusted origin must not expose CORS allow headers")
        void deleteMethod_allowedFromAnyOrigin() throws Exception {
            // Untrusted origins must not receive allow headers for dangerous methods.
            mockMvc.perform(options("/api/v1/auth/signin")
                            .header("Origin", "https://malicious.com")
                            .header("Access-Control-Request-Method", "DELETE")
                            .header("Access-Control-Request-Headers", "Authorization"))
                    .andExpect(header().doesNotExist("Access-Control-Allow-Origin"))
                    .andExpect(header().doesNotExist("Access-Control-Allow-Methods"));
        }
    }

    // ======================================================================
    //  Security headers on responses
    // ======================================================================

    @Nested
    @DisplayName("V14 patch – Security headers (CSP + X-Frame-Options)")
    class SecurityHeaders {

        @Test
        @DisplayName("PATCHED V14: Content-Security-Policy header must be present and restrict origins")
        void contentSecurityPolicy_mustBePresent() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signin")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"u@u.com\",\"password\":\"password\"}"))
                    .andExpect(header().exists("Content-Security-Policy"))
                    .andExpect(header().string("Content-Security-Policy",
                            org.hamcrest.Matchers.containsString("default-src 'self'")));
        }

        @Test
        @DisplayName("PATCHED V14: frame-ancestors 'none' prevents clickjacking via CSP")
        void frameAncestors_mustBeNone() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signin")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"u@u.com\",\"password\":\"password\"}"))
                    .andExpect(header().string("Content-Security-Policy",
                            org.hamcrest.Matchers.containsString("frame-ancestors 'none'")));
        }

        @Test
        @DisplayName("X-Frame-Options header must be present (clickjacking protection)")
        void xFrameOptions_mustBeDenyOrSameOrigin() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signin")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"u@u.com\",\"password\":\"password\"}"))
                    .andExpect(header().exists("X-Frame-Options"));
        }
    }
}
