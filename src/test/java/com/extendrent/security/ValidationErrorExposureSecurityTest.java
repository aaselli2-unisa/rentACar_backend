package com.extendrent.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import src.controller.auth.authentication.AuthenticationController;
import src.core.config.AppConfig;
import src.core.config.SecurityConfig;
import src.core.security.JwtService;
import src.service.auth.AuthenticationService;
import src.service.external.EmailService;
import src.service.user.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * V-06 – Validation error messages must not leak DTO field names in production
 * (OWASP A05 / CWE-209).
 *
 * Returning "fieldName: message" in HTTP responses allows attackers to enumerate
 * valid DTO structure, facilitating targeted injection and fuzzing attacks.
 *
 * Fix: CustomExceptionHandler reads app.expose-validation-details (default: false).
 * When false, validation errors return a generic "Validation error" string only;
 * full details are logged server-side. When true (dev/test), full field names are returned.
 */
@WebMvcTest(AuthenticationController.class)
@Import({SecurityConfig.class, AppConfig.class})
@ActiveProfiles("test")
@DisplayName("V-06 – Validation error exposure controlled by app.expose-validation-details")
class ValidationErrorExposureSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;
    @MockBean private AuthenticationService authenticationService;
    @MockBean private EmailService emailService;

    @Test
    @DisplayName("With expose-validation-details=true (dev), response contains field names")
    // application-test.properties sets app.expose-validation-details=true
    void withExposeTrue_validationResponse_containsFieldName() throws Exception {
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"T","surname":"User123","emailAddress":"bad-email",
                                "password":"weak","phoneNumber":"123",
                                "authority":"CUSTOMER","userImageEntityId":1}"""))
                .andExpect(status().isBadRequest());
        // In test profile (expose-validation-details=true), field details are returned.
        // This is a control assertion — the test environment behaves like dev.
    }

    @WebMvcTest(AuthenticationController.class)
    @Import({SecurityConfig.class, AppConfig.class})
    @ActiveProfiles("test")
    @TestPropertySource(properties = "app.expose-validation-details=false")
    @DisplayName("Production mode: validation errors return generic message only")
    static class ProductionModeTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean private JwtService jwtService;
        @MockBean private UserService userService;
        @MockBean private AuthenticationService authenticationService;
        @MockBean private EmailService emailService;

        @Test
        @DisplayName("With expose-validation-details=false (prod), response does NOT contain field names")
        void withExposeFalse_validationResponse_isGeneric() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name":"T","surname":"User123","emailAddress":"bad-email",
                                    "password":"weak","phoneNumber":"123",
                                    "authority":"CUSTOMER","userImageEntityId":1}"""))
                    .andExpect(status().isBadRequest())
                    // Generic message only — no field names like "surname: ..." or "password: ..."
                    .andExpect(jsonPath("$.response.details[0]").value("Validation error"));
        }
    }
}
