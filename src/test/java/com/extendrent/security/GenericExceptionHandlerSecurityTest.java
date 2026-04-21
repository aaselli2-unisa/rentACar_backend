package com.extendrent.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import src.controller.user.UserController;
import src.core.config.AppConfig;
import src.core.config.SecurityConfig;
import src.core.security.JwtService;
import src.service.user.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security regression test for V08 — Generic exception handler exposes e.getMessage() in 500 responses.
 *
 * OWASP A05 – Security Misconfiguration | CWE-209 – Generation of Error Message Containing
 * Sensitive Information
 *
 * CustomExceptionHandler.handleException() returns the raw Java exception message in the
 * response body. Exception messages frequently contain database query text, table/column
 * names, JDBC driver details, or internal class paths — all useful to an attacker.
 *
 * THIS TEST PASSES — it became green after V08 was patched: e.getMessage() was replaced with a
 * static generic string: "An internal error occurred".
 */
@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, AppConfig.class})
@ActiveProfiles("test")
@DisplayName("V08 – 500 response exposes e.getMessage() (OWASP A05 / CWE-209)")
class GenericExceptionHandlerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;

    @Test
    @DisplayName("PATCHED V08: 500 response must NOT contain JDBC error details from exception message")
    @WithMockUser(roles = "ADMIN")
    void internalServerError_mustNotExposeJdbcDetails() throws Exception {
        String sensitiveMessage =
                "ERROR: column \"secret_column\" of relation \"users\" does not exist at line 42";

        when(userService.getAll(any(Pageable.class)))
                .thenThrow(new RuntimeException(sensitiveMessage));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> {
                    String body = result.getResponse().getContentAsString();
                    assertThat(body)
                            .as("500 body must not contain JDBC column details. Body was: %s", body)
                            .doesNotContain("secret_column");
                    assertThat(body)
                            .as("500 body must not contain table name 'users'. Body was: %s", body)
                            .doesNotContain("relation \"users\"");
                    assertThat(body)
                            .as("500 body must not expose the raw exception message. Body was: %s", body)
                            .doesNotContain(sensitiveMessage);
                });
    }

    @Test
    @DisplayName("PATCHED V08: 500 response must NOT contain Java class names or stack-trace markers")
    @WithMockUser(roles = "ADMIN")
    void internalServerError_mustNotExposeClassNames() throws Exception {
        when(userService.getAll(any(Pageable.class)))
                .thenThrow(new RuntimeException(
                        "NullPointerException in src.service.user.UserServiceImpl.getAll line 88"));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> {
                    String body = result.getResponse().getContentAsString();
                    assertThat(body)
                            .as("500 body must not expose internal class names. Body was: %s", body)
                            .doesNotContain("UserServiceImpl");
                    assertThat(body)
                            .as("500 body must not contain 'NullPointerException'. Body was: %s", body)
                            .doesNotContain("NullPointerException");
                });
    }

    @Test
    @DisplayName("500 response must return a generic message and must NOT reflect the exception message")
    @WithMockUser(roles = "ADMIN")
    void internalServerError_mustReturnGenericMessage() throws Exception {
        when(userService.getAll(any(Pageable.class)))
                .thenThrow(new RuntimeException("super sensitive internal detail"));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> {
                    String body = result.getResponse().getContentAsString();
                    assertThat(body)
                            .as("500 body must not echo the raw exception message")
                            .doesNotContain("super sensitive internal detail");
                    assertThat(body)
                            .as("500 body must not contain Java class names")
                            .doesNotContain("RuntimeException")
                            .doesNotContain("at src.");
                    assertThat(body)
                            .as("500 body must not be empty — a safe message must be returned")
                            .isNotEmpty();
                });
    }
}
