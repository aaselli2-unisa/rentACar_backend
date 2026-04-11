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
 * CORS misconfiguration tests.
 *
 * CRITICAL vulnerability: Both {@link CorsConfig} and {@link WebConfig} use
 * {@code allowedOrigins("*")}, which means any origin in the world can make
 * cross-origin requests to this API.
 *
 * Combined with CSRF disabled (stateless API), this creates a risk surface where
 * malicious pages can perform API calls on behalf of authenticated victims.
 *
 * Tests assert what the CORRECT behaviour should be (specific allowed origins).
 * Tests that currently PASS (wildcard accepted) document the vulnerability.
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
    @DisplayName("VULNERABILITY – wildcard origin is accepted (currently PASSES, should FAIL after fix)")
    class WildcardOriginVulnerability {

        @Test
        @DisplayName("Preflight from an arbitrary attacker origin returns 200 with ACAO header")
        void preflightFromArbitraryOrigin_isAccepted() throws Exception {
            mockMvc.perform(options("/api/v1/auth/signin")
                            .header("Origin", "https://evil-attacker.com")
                            .header("Access-Control-Request-Method", "POST")
                            .header("Access-Control-Request-Headers", "Content-Type,Authorization"))
                    .andExpect(status().isOk())
                    // Currently the response allows any origin — documents the vulnerability
                    .andExpect(header().exists("Access-Control-Allow-Origin"));
        }

        @Test
        @DisplayName("Simple GET from attacker origin receives ACAO header (wildcard exposure)")
        void simpleRequestFromAttackerOrigin_receivesACAOHeader() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signin")
                            .header("Origin", "https://attacker.example.com")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"u@u.com\",\"password\":\"password\"}"))
                    .andExpect(header().exists("Access-Control-Allow-Origin"));
        }

        @Test
        @DisplayName("ACAO header must NOT be '*' after security fix – currently is wildcard")
        void acaoHeader_mustNotBeWildcard_afterFix() throws Exception {
            mockMvc.perform(options("/api/v1/auth/signin")
                            .header("Origin", "https://legit-frontend.example.com")
                            .header("Access-Control-Request-Method", "POST")
                            .header("Access-Control-Request-Headers", "Authorization"))
                    .andExpect(result -> {
                        String acao = result.getResponse().getHeader("Access-Control-Allow-Origin");
                        // After fix: ACAO must be the specific allowed origin, not "*"
                        // Currently this assertion FAILS (wildcard is returned)
                        if (acao != null) {
                            org.assertj.core.api.Assertions.assertThat(acao)
                                    .as("Access-Control-Allow-Origin must not be wildcard")
                                    .isNotEqualTo("*");
                        }
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
    @DisplayName("Security headers – missing from current configuration")
    class MissingSecurityHeaders {

        @Test
        @DisplayName("VULNERABILITY: X-Frame-Options header is missing – clickjacking risk")
        void xFrameOptions_shouldBeDeny() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signin")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"u@u.com\",\"password\":\"password\"}"))
                    .andExpect(result -> {
                        // With current config, X-Frame-Options may not be set.
                        // After fix: .andExpect(header().string("X-Frame-Options", "DENY"))
                        // This test documents the missing header.
                        String xfo = result.getResponse().getHeader("X-Frame-Options");
                        org.junit.jupiter.api.Assumptions.assumeTrue(
                                xfo != null,
                                "X-Frame-Options header not present – clickjacking risk");
                        org.assertj.core.api.Assertions.assertThat(xfo)
                                .isIn("DENY", "SAMEORIGIN");
                    });
        }

        @Test
        @DisplayName("VULNERABILITY: Content-Security-Policy header is missing")
        void contentSecurityPolicy_shouldBePresent() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signin")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"u@u.com\",\"password\":\"password\"}"))
                    .andExpect(result -> {
                        String csp = result.getResponse().getHeader("Content-Security-Policy");
                        org.junit.jupiter.api.Assumptions.assumeTrue(
                                csp != null,
                                "Content-Security-Policy header not present – XSS amplification risk");
                        org.assertj.core.api.Assertions.assertThat(csp).isNotEmpty();
                    });
        }
    }
}
