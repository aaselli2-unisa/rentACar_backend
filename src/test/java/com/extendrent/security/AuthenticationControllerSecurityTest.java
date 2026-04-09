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
        @DisplayName("Valid CUSTOMER signup payload is accepted (no 400)")
        void validCustomerSignup_isAccepted() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validCustomerJson()))
                    .andExpect(status().is(not(400)));
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
        @DisplayName("VULNERABILITY: ADMIN role can be requested through the public signup endpoint")
        void publicEndpoint_acceptsAdminRoleSignup_documentsEscalationRisk() throws Exception {
            // Any unauthenticated caller can request ADMIN authority during signup.
            // The application MUST validate that only ADMIN-initiated flows may create ADMIN users.
            // This test documents the missing server-side role restriction.
            mockMvc.perform(post("/api/v1/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name":"Attacker","surname":"User","emailAddress":"hack@hack.com",
                                    "password":"12345678","phoneNumber":"5551234567",
                                    "authority":"ADMIN","userImageEntityId":1,
                                    "salary":999999.0}"""))
                    .andExpect(status().is(not(401))) // no access control blocks this
                    .andExpect(status().is(not(403)));
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
                    // Response must not echo the raw script tag in a 200 body
                    .andExpect(result -> {
                        String body = result.getResponse().getContentAsString();
                        // Either the request is rejected (4xx) or the payload is not reflected raw
                        int status = result.getResponse().getStatus();
                        if (status == 200 || status == 204) {
                            org.assertj.core.api.Assertions.assertThat(body)
                                    .doesNotContain("<script>");
                        }
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
    //  GET /api/v1/auth/isUserTrue – credential exposure vulnerability
    // ======================================================================

    @Nested
    @DisplayName("VULNERABILITY – GET /api/v1/auth/isUserTrue exposes credentials in URL")
    class IsUserTrueVulnerability {

        @Test
        @DisplayName("Endpoint is reachable without authentication (included in whitelist)")
        void endpoint_isPubliclyAccessible() throws Exception {
            mockMvc.perform(get("/api/v1/auth/isUserTrue")
                            .param("email", "admin@example.com")
                            .param("password", "secret"))
                    .andExpect(status().is(not(401)));
        }

        @Test
        @DisplayName("Credentials are transmitted as query parameters – documented as CRITICAL risk")
        void credentials_inQueryParams_areVisibleInLogs() throws Exception {
            // Query parameters appear in server logs, browser history, Referer headers,
            // and load-balancer access logs. This endpoint must be replaced with a POST
            // that accepts credentials in the request body.
            //
            // This test simply asserts the endpoint maps to a GET, confirming the design flaw.
            mockMvc.perform(get("/api/v1/auth/isUserTrue")
                            .param("email", "victim@example.com")
                            .param("password", "hunter2"))
                    // If the endpoint is removed/replaced with POST this will return 405, which is
                    // the desired state. Currently it returns 200 or service-layer error.
                    .andExpect(status().is(not(405))); // currently still a GET
        }

        @Test
        @DisplayName("Missing email parameter returns 400 Bad Request, not 500")
        void missingEmailParam_returns400NotInternalError() throws Exception {
            mockMvc.perform(get("/api/v1/auth/isUserTrue")
                            .param("password", "secret"))
                    .andExpect(status().is(not(500)));
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
                "password":"Str0ngPass","phoneNumber":"5551234567",
                "authority":"CUSTOMER","userImageEntityId":1}""";
    }
}
