package com.extendrent.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import src.controller.auth.authentication.AuthenticationController;
import src.core.config.AppConfig;
import src.core.config.SecurityConfig;
import src.core.security.JwtService;
import src.service.auth.AuthenticationService;
import src.service.external.EmailService;
import src.service.user.UserService;
import src.service.user.model.UserRole;

import jakarta.servlet.http.Cookie;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * V-13 – Swagger UI must require ADMIN role, not just authenticated() (OWASP A05 / CWE-284).
 *
 * Previously Swagger was accessible to any authenticated user, including CUSTOMER role.
 * Swagger exposes the full API contract (all endpoints, request/response schemas, auth flows),
 * enabling rapid enumeration and targeted exploitation by low-privilege accounts.
 *
 * Fix: SecurityConfig changes .authenticated() to .hasRole("ADMIN") for Swagger paths.
 */
@WebMvcTest(AuthenticationController.class)
@Import({SecurityConfig.class, AppConfig.class})
@ActiveProfiles("test")
@DisplayName("V-13 – Swagger UI must be restricted to ADMIN role")
class SwaggerAdminOnlySecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;
    @MockBean private AuthenticationService authenticationService;
    @MockBean private EmailService emailService;

    @Test
    @DisplayName("Unauthenticated request to /swagger-ui/index.html returns 401")
    void swaggerUi_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("CUSTOMER token on /swagger-ui/** returns 403 Forbidden (not just authenticated)")
    void swaggerUi_customerRole_returns403() throws Exception {
        String token = SecurityTestSupport.validJwt("customer@example.com", UserRole.CUSTOMER);
        SecurityTestSupport.setupAuthMocks(jwtService, userService, token,
                SecurityTestSupport.userEntity("customer@example.com", UserRole.CUSTOMER));

        mockMvc.perform(get("/swagger-ui/index.html")
                        .cookie(new Cookie("accessToken", token)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("CUSTOMER token on /v3/api-docs returns 403 Forbidden")
    void apiDocs_customerRole_returns403() throws Exception {
        String token = SecurityTestSupport.validJwt("customer@example.com", UserRole.CUSTOMER);
        SecurityTestSupport.setupAuthMocks(jwtService, userService, token,
                SecurityTestSupport.userEntity("customer@example.com", UserRole.CUSTOMER));

        mockMvc.perform(get("/v3/api-docs")
                        .cookie(new Cookie("accessToken", token)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ADMIN token on /swagger-ui/** returns 2xx or 404 (not 401/403)")
    void swaggerUi_adminRole_isAllowed() throws Exception {
        String token = SecurityTestSupport.validJwt("admin@example.com", UserRole.ADMIN);
        SecurityTestSupport.setupAuthMocks(jwtService, userService, token,
                SecurityTestSupport.userEntity("admin@example.com", UserRole.ADMIN));

        int status = mockMvc.perform(get("/swagger-ui/index.html")
                        .cookie(new Cookie("accessToken", token)))
                .andReturn().getResponse().getStatus();

        // In test context, the actual Swagger UI may not be fully registered,
        // so 404 is acceptable. What we verify is that auth does NOT return 401 or 403.
        org.assertj.core.api.Assertions.assertThat(status)
                .as("ADMIN must not be denied access to Swagger (got %d)", status)
                .isNotIn(401, 403);
    }
}
