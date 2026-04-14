package com.extendrent.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

/**
 * Security regression tests for V03 — Attacker domains explicitly whitelisted in CORS.
 *
 * OWASP A05 – Security Misconfiguration | CWE-942 – Permissive Cross-domain Policy with Untrusted Domains
 *
 * Both CorsConfig and WebConfig list "https://evil-attacker.com" and
 * "https://attacker.example.com" as explicitly allowed origins. A malicious page
 * served from either domain can read full API responses via credentialed cross-origin
 * requests (tokens, PII, financial data).
 *
 * THIS TEST PASSES — it became green after V03 was patched (attacker domains removed from CORS whitelist)
 * from ALLOWED_ORIGINS in CorsConfig and WebConfig.
 */
@WebMvcTest(AuthenticationController.class)
@Import({SecurityConfig.class, AppConfig.class, CorsConfig.class, WebConfig.class})
@ActiveProfiles("test")
@DisplayName("V03 – Attacker domains in CORS whitelist (OWASP A05 / CWE-942)")
class CorsAttackerDomainSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;
    @MockBean private AuthenticationService authenticationService;
    @MockBean private EmailService emailService;

    @Test
    @DisplayName("PATCHED V03: preflight from evil-attacker.com must NOT return Access-Control-Allow-Origin")
    void preflight_fromEvilAttackerCom_mustBeRejected() throws Exception {
        mockMvc.perform(options("/api/v1/auth/signin")
                        .header("Origin", "https://evil-attacker.com")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "Authorization,Content-Type"))
                // After fix: this origin is not in ALLOWED_ORIGINS → no ACAO header
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    @Test
    @DisplayName("PATCHED V03: preflight from attacker.example.com must NOT return Access-Control-Allow-Origin")
    void preflight_fromAttackerExampleCom_mustBeRejected() throws Exception {
        mockMvc.perform(options("/api/v1/auth/signin")
                        .header("Origin", "https://attacker.example.com")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "Authorization"))
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    @Test
    @DisplayName("PATCHED V03: simple request from evil-attacker.com must NOT reflect ACAO header")
    void simpleRequest_fromEvilAttackerCom_mustNotReflectAcao() throws Exception {
        mockMvc.perform(post("/api/v1/auth/signin")
                        .header("Origin", "https://evil-attacker.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"u@u.com\",\"password\":\"password\"}"))
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    @Test
    @DisplayName("PASSES: preflight from legitimate frontend must still be allowed after fix")
    void preflight_fromLegitFrontend_mustBeAllowed() throws Exception {
        mockMvc.perform(options("/api/v1/auth/signin")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "Authorization,Content-Type"))
                .andExpect(header().exists("Access-Control-Allow-Origin"));
    }
}
