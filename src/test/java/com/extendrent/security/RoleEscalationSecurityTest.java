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
import src.core.config.SecurityConfig;
import src.core.security.JwtService;
import src.service.auth.AuthenticationService;
import src.service.external.EmailService;
import src.service.user.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security regression test for V02 — Role escalation via public signup endpoint.
 *
 * OWASP A01 – Broken Access Control | CWE-269 – Improper Privilege Management
 *
 * The public POST /api/v1/auth/signup endpoint accepts a free-choice {@code authority}
 * field. Any anonymous caller can pass {@code "authority":"ADMIN"} and obtain a
 * full administrator account.
 *
 * THIS TEST PASSES — the vulnerability is fixed (V02) and this is now a regression guard
 * once {@code CustomAuthenticationServiceImpl.signUp()} forces the role to CUSTOMER.
 */
@WebMvcTest(AuthenticationController.class)
@Import({SecurityConfig.class, AppConfig.class})
@ActiveProfiles("test")
@DisplayName("V02 – Role escalation via public signup (OWASP A01 / CWE-269)")
class RoleEscalationSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;
    @MockBean private AuthenticationService authenticationService;
    @MockBean private EmailService emailService;

    @Test
    @DisplayName("PATCHED V02: POST /signup with authority=ADMIN returns 400 (role escalation blocked)")
    void signup_withAdminAuthority_mustBeRejected() throws Exception {
        // V02 patch: @AssertTrue on isAuthorityCustomer() rejects any authority != CUSTOMER.
        // Regression guard: if the annotation is removed, this test turns red immediately.
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name":"Attacker",
                                  "surname":"User",
                                  "emailAddress":"attacker@evil.com",
                                  "password":"Str0ng@Pass",
                                  "phoneNumber":"5551234567",
                                  "authority":"ADMIN",
                                  "userImageEntityId":1,
                                  "salary":99999.0
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String body = result.getResponse().getContentAsString();
                    assertThat(body)
                            .as("400 body must mention the rejected field (authorityCustomer) or the"
                                    + " constraint message (CUSTOMER), confirming rejection is for the"
                                    + " authority field, not an unrelated validation error")
                            .satisfiesAnyOf(
                                    b -> assertThat(b).containsIgnoringCase("authorityCustomer"),
                                    b -> assertThat(b).containsIgnoringCase("CUSTOMER")
                            );
                });
    }

    @Test
    @DisplayName("PATCHED V02: POST /signup with authority=EMPLOYEE returns 400 (role escalation blocked)")
    void signup_withEmployeeAuthority_mustBeRejected() throws Exception {
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name":"Attacker",
                                  "surname":"User",
                                  "emailAddress":"emp@evil.com",
                                  "password":"Str0ng@Pass",
                                  "phoneNumber":"5551234567",
                                  "authority":"EMPLOYEE",
                                  "userImageEntityId":1,
                                  "salary":50000.0
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PASSES: POST /signup with authority=CUSTOMER must be accepted")
    void signup_withCustomerAuthority_mustBeAccepted() throws Exception {
        // After fix, CUSTOMER is the only role allowed on the public endpoint.
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name":"Alice",
                                  "surname":"Smith",
                                  "emailAddress":"alice@example.com",
                                  "password":"Str0ng@Pass",
                                  "phoneNumber":"5551234567",
                                  "authority":"CUSTOMER",
                                  "userImageEntityId":1
                                }
                                """))
                .andExpect(status().is2xxSuccessful());
    }
}
