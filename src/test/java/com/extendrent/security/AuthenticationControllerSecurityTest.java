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
import src.core.config.AppConfig;
import src.core.config.SecurityConfig;
import src.core.security.JwtService;
import src.service.auth.AuthenticationService;
import src.service.external.EmailService;
import src.service.user.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Security tests for the authentication controller.
 *
 * Covers:
 * - Bean-validation enforcement on signup/signin requests
 * - Credentials-in-URL vulnerability (isUserTrue endpoint)
 * - Role escalation via signup (requesting ADMIN role through the public endpoint)
 * - Response does not leak stack traces or internal error details on bad input
 */
@WebMvcTest(AuthenticationController.class)
@Import({SecurityConfig.class, AppConfig.class})
@ActiveProfiles("test")
@DisplayName("AuthenticationController – security tests")
class AuthenticationControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;
    @MockBean private AuthenticationService authenticationService;
    @MockBean private EmailService emailService;

    // ======================================================================
    //  POST /api/v1/auth/signup – input validation
    // ======================================================================

    @Nested
    @DisplayName("POST /api/v1/auth/signup – bean-validation enforcement")
    class SignupValidation {

        @Test
        @DisplayName("Valid CUSTOMER signup payload is accepted (2xx)")
        void validCustomerSignup_isAccepted() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validCustomerJson()))
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        @DisplayName("Missing email returns 400 Bad Request")
        void missingEmail_returns400() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name":"Test","surname":"User","password":"12345678",
                                    "phoneNumber":"5551234567","authority":"CUSTOMER","userImageEntityId":1}"""))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Invalid email format returns 400 Bad Request")
        void invalidEmail_returns400() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name":"Test","surname":"User","emailAddress":"not-an-email",
                                    "password":"12345678","phoneNumber":"5551234567",
                                    "authority":"CUSTOMER","userImageEntityId":1}"""))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Password shorter than 8 characters returns 400 Bad Request")
        void shortPassword_returns400() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name":"Test","surname":"User","emailAddress":"t@t.com",
                                    "password":"abc","phoneNumber":"5551234567",
                                    "authority":"CUSTOMER","userImageEntityId":1}"""))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Phone number with non-digits returns 400 Bad Request")
        void nonDigitPhone_returns400() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name":"Test","surname":"User","emailAddress":"t@t.com",
                                    "password":"12345678","phoneNumber":"555-12-34",
                                    "authority":"CUSTOMER","userImageEntityId":1}"""))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Phone number with fewer than 10 digits returns 400 Bad Request")
        void shortPhone_returns400() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name":"Test","surname":"User","emailAddress":"t@t.com",
                                    "password":"12345678","phoneNumber":"12345",
                                    "authority":"CUSTOMER","userImageEntityId":1}"""))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Surname containing digits returns 400 Bad Request")
        void surnameWithDigits_returns400() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name":"Test","surname":"User123","emailAddress":"t@t.com",
                                    "password":"12345678","phoneNumber":"5551234567",
                                    "authority":"CUSTOMER","userImageEntityId":1}"""))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Missing authority field returns 400 Bad Request")
        void missingAuthority_returns400() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name":"Test","surname":"User","emailAddress":"t@t.com",
                                    "password":"12345678","phoneNumber":"5551234567",
                                    "userImageEntityId":1}"""))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("PATCHED V02: signup with ADMIN authority returns 400 Bad Request")
        void adminRoleSignup_isRejectedWithBadRequest() throws Exception {
            // Security patch V02: @AssertTrue on isAuthorityCustomer() ensures only CUSTOMER
            // is accepted. Sending ADMIN triggers a MethodArgumentNotValidException → 400.
            // This is a regression guard for V02 — if the @AssertTrue annotation is removed,
            // this test turns red immediately.
            mockMvc.perform(post("/api/v1/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name":"Attacker","surname":"User","emailAddress":"hack@hack.com",
                                    "password":"12345678","phoneNumber":"5551234567",
                                    "authority":"ADMIN","userImageEntityId":1,
                                    "salary":999999.0}"""))
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest(name = "XSS payload in name: {0}")
        @ValueSource(strings = {
                "<script>alert(1)</script>",
                "javascript:alert(1)",
                "<img src=x onerror=alert(1)>"
        })
        @DisplayName("XSS payloads in name field – controller layer must not reflect them unescaped")
        void xssInName_isHandledSafely(String payload) throws Exception {
            String json = """
                    {"name":"%s","surname":"User","emailAddress":"t@t.com",
                    "password":"12345678","phoneNumber":"5551234567",
                    "authority":"CUSTOMER","userImageEntityId":1}""".formatted(
                    payload.replace("\"", "\\\""));

            mockMvc.perform(post("/api/v1/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    // XSS payloads must not be reflected unescaped regardless of status code.
                    // Validation error bodies (400) must not echo the field value back.
                    .andExpect(result -> {
                        String body = result.getResponse().getContentAsString();
                        org.assertj.core.api.Assertions.assertThat(body)
                                .as("XSS payload must not be reflected unescaped in response body "
                                        + "(status: %d)", result.getResponse().getStatus())
                                .doesNotContain("<script>")
                                .doesNotContain("onerror=")
                                .doesNotContain("javascript:");
                    });
        }
    }

    // ======================================================================
    //  POST /api/v1/auth/signin – input validation
    // ======================================================================

    @Nested
    @DisplayName("POST /api/v1/auth/signin – bean-validation enforcement")
    class SigninValidation {

        @Test
        @DisplayName("Missing email returns 400 Bad Request")
        void missingEmail_returns400() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"password\":\"password\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Blank email returns 400 Bad Request")
        void blankEmail_returns400() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"\",\"password\":\"password\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Missing password returns 400 Bad Request")
        void missingPassword_returns400() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"user@example.com\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Blank password returns 400 Bad Request")
        void blankPassword_returns400() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"user@example.com\",\"password\":\"\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Invalid email format returns 400 Bad Request")
        void invalidEmail_returns400() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"not-an-email\",\"password\":\"password\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Empty JSON body returns 400 Bad Request")
        void emptyBody_returns400() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }
    }

    // ======================================================================
    //  POST /api/v1/auth/isUserTrue – V01 security patch verification
    //  Credentials moved from query parameters to request body.
    // ======================================================================

    @Nested
    @DisplayName("V01 patch – POST /api/v1/auth/isUserTrue (credentials in body, not URL)")
    class IsUserTruePatch {

        @Test
        @DisplayName("GET /api/v1/auth/isUserTrue is blocked (old query-param endpoint removed)")
        void oldGet_withQueryParams_isBlocked() throws Exception {
            // Security patch V01: the GET mapping was removed.
            // Spring Security may return 401 before the dispatcher can return 405 —
            // both are acceptable since the request does not succeed.
            int status = mockMvc.perform(get("/api/v1/auth/isUserTrue")
                            .param("email", "admin@example.com")
                            .param("password", "secret"))
                    .andReturn().getResponse().getStatus();
            assertThat(status).as("GET /api/v1/auth/isUserTrue must not return 200").isNotEqualTo(200);
            assertThat(status).as("Expected 401 or 405, got " + status)
                    .isIn(401, 405);
        }

        @Test
        @DisplayName("POST /api/v1/auth/isUserTrue with JSON body is accepted (password in body, not URL)")
        void newPost_withJsonBody_isAccepted() throws Exception {
            // Security patch V01: POST with request body — password never appears in URL.
            mockMvc.perform(post("/api/v1/auth/isUserTrue")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"user@example.com\",\"password\":\"secret\"}"))
                    .andExpect(status().is(not(405)))
                    .andExpect(status().is(not(400)));
        }

        @Test
        @DisplayName("POST /api/v1/auth/isUserTrue with missing password returns 400 Bad Request")
        void missingPassword_returns400() throws Exception {
            mockMvc.perform(post("/api/v1/auth/isUserTrue")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"user@example.com\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST /api/v1/auth/isUserTrue with empty body returns 400 Bad Request")
        void emptyBody_returns400() throws Exception {
            mockMvc.perform(post("/api/v1/auth/isUserTrue")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }
    }

    // ======================================================================
    //  Content-type enforcement
    // ======================================================================

    @Nested
    @DisplayName("Content-type enforcement")
    class ContentTypeEnforcement {

        @Test
        @DisplayName("Signup with text/plain content-type returns 415 Unsupported Media Type")
        void wrongContentType_returns415() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signup")
                            .contentType(MediaType.TEXT_PLAIN)
                            .content("raw text body"))
                    .andExpect(status().isUnsupportedMediaType());
        }
    }

    // ---- helpers -----------------------------------------------------------

    private static String validCustomerJson() {
        return """
                {"name":"Alice","surname":"Smith","emailAddress":"alice@example.com",
                "password":"Str0ng@Pass","phoneNumber":"5551234567",
                "authority":"CUSTOMER","userImageEntityId":1}""";
    }
}
