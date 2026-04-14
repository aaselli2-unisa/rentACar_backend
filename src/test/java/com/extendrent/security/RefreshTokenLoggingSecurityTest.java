package com.extendrent.security;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import src.controller.auth.token.RefreshTokenController;
import src.core.config.AppConfig;
import src.core.config.SecurityConfig;
import src.core.security.JwtService;
import src.core.security.model.JwtToken;
import src.service.auth.AccessTokenService;
import src.service.user.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Security regression test for V05 — Refresh token value logged in plain text.
 *
 * OWASP A09 – Security Logging and Monitoring Failures | CWE-532 – Insertion of Sensitive
 * Information into Log File
 *
 * RefreshTokenController logs the full token value:
 *   log.info(REFRESH_TOKEN_REQUEST_RECEIVED, refreshTokenRequest.getToken());
 *
 * Anyone with read access to application logs (ops, SIEM, log vendors) can steal
 * a valid token and impersonate the user without knowing their password.
 *
 * THIS TEST PASSES — it became green after V05 was patched: the token value was removed
 * from the log.info call in RefreshTokenController.
 */
@WebMvcTest(RefreshTokenController.class)
@Import({SecurityConfig.class, AppConfig.class})
@ActiveProfiles("test")
@DisplayName("V05 – Refresh token logged in plain text (OWASP A09 / CWE-532)")
class RefreshTokenLoggingSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;
    @MockBean private AccessTokenService accessTokenService;

    private ListAppender<ILoggingEvent> logAppender;
    private Logger refreshTokenLogger;

    @BeforeEach
    void attachLogAppender() {
        refreshTokenLogger = (Logger) LoggerFactory.getLogger(RefreshTokenController.class);
        logAppender = new ListAppender<>();
        logAppender.start();
        refreshTokenLogger.addAppender(logAppender);
    }

    @AfterEach
    void detachLogAppender() {
        refreshTokenLogger.detachAppender(logAppender);
    }

    @Test
    @DisplayName("PATCHED V05: refresh token value must NOT appear in any log message")
    void refreshTokenRequest_tokenValueMustNotBeLogged() throws Exception {
        String sensitiveToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.SECRET_PAYLOAD.SIGNATURE_XYZ";

        when(accessTokenService.refreshToken(any()))
                .thenReturn(JwtToken.builder().token("new-access-token").build());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .post("/api/v1/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "user@example.com",
                                  "token": "%s"
                                }
                                """.formatted(sensitiveToken)));

        List<ILoggingEvent> logs = logAppender.list;

        // The token value must never appear in any log statement
        logs.forEach(event -> {
            String message = event.getFormattedMessage();
            assertThat(message)
                    .as("Log message must not contain the refresh token value: [%s]", message)
                    .doesNotContain(sensitiveToken);
        });
    }

    @Test
    @DisplayName("PASSES after fix: no log message contains the token value (token is not logged)")
    void refreshTokenRequest_noLogMessageContainsTokenValue() throws Exception {
        String tokenValue = "some-token-value-that-must-not-be-logged";

        when(accessTokenService.refreshToken(any()))
                .thenReturn(JwtToken.builder().token("new-access-token").build());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .post("/api/v1/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "user@example.com",
                                  "token": "%s"
                                }
                                """.formatted(tokenValue)))
                .andReturn(); // execute the request and capture result

        // After fix: none of the log messages may contain the raw token value
        logAppender.list.forEach(event -> {
            String message = event.getFormattedMessage();
            assertThat(message)
                    .as("Log message must not contain the refresh token value: [%s]", message)
                    .doesNotContain(tokenValue);
        });
    }
}
