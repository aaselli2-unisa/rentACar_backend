package com.extendrent.security;

import org.junit.jupiter.api.DisplayName;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * V-08 – Password complexity enforcement (OWASP A07 / CWE-521).
 *
 * Previously, SignUpReqeust validated only length (8-30 chars), allowing passwords
 * like "aaaaaaaa" or "12345678" that are trivially brute-forceable.
 *
 * Fix: @Pattern enforces at least one uppercase, one lowercase, one digit, and one
 * special character — making dictionary and simple brute-force attacks infeasible.
 *
 * V-12 – Phone number must not start with 0.
 * Fix: regex changed from ^[0-9]+$ to ^[1-9][0-9]{9}$ to reject all-zeros and
 * other trivially invalid numbers.
 */
@WebMvcTest(AuthenticationController.class)
@Import({SecurityConfig.class, AppConfig.class})
@ActiveProfiles("test")
@DisplayName("V-08/V-12 – Password complexity and phone validation enforcement")
class PasswordComplexitySecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;
    @MockBean private AuthenticationService authenticationService;
    @MockBean private EmailService emailService;

    // -----------------------------------------------------------------------
    //  V-08: Password complexity
    // -----------------------------------------------------------------------

    @ParameterizedTest(name = "Weak password ''{0}'' returns 400 Bad Request")
    @ValueSource(strings = {
            "aaaaaaaa",          // no uppercase, no digit, no special char
            "12345678",          // no letter, no special char
            "Abcdefgh",          // no digit, no special char
            "Abcdefg1",          // no special char
            "abcdef1!",          // no uppercase
            "ABCDEF1!",          // no lowercase
            "Ab1!",              // too short (< 8 chars)
    })
    @DisplayName("V-08: Weak passwords are rejected with 400 Bad Request")
    void weakPasswords_areRejected(String weakPassword) throws Exception {
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupJson(weakPassword, "5551234567")))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest(name = "Strong password ''{0}'' is accepted")
    @ValueSource(strings = {
            "Str0ng@Pass",
            "P@ssw0rd!",
            "Secur3!ty",
            "MyP@ss1234",
    })
    @DisplayName("V-08: Strong passwords satisfying all complexity requirements are accepted")
    void strongPasswords_areAccepted(String strongPassword) throws Exception {
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupJson(strongPassword, "5551234567")))
                .andExpect(status().is2xxSuccessful());
    }

    // -----------------------------------------------------------------------
    //  V-12: Phone number validation
    // -----------------------------------------------------------------------

    @ParameterizedTest(name = "Invalid phone ''{0}'' returns 400 Bad Request")
    @ValueSource(strings = {
            "0000000000",   // all zeros — was previously accepted
            "0123456789",   // starts with 0 — rejected by new regex
            "123456789",    // only 9 digits
    })
    @DisplayName("V-12: Phone numbers starting with 0 or fewer than 10 digits are rejected")
    void invalidPhone_isRejected(String phone) throws Exception {
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupJson("Str0ng@Pass", phone)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("V-12: Valid 10-digit phone not starting with 0 is accepted")
    void validPhone_isAccepted() throws Exception {
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupJson("Str0ng@Pass", "5551234567")))
                .andExpect(status().is2xxSuccessful());
    }

    private static String signupJson(String password, String phone) {
        return """
                {"name":"Alice","surname":"Smith","emailAddress":"alice@example.com",
                "password":"%s","phoneNumber":"%s",
                "authority":"CUSTOMER","userImageEntityId":1}""".formatted(password, phone);
    }
}
