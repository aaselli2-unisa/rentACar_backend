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
import org.springframework.test.web.servlet.MvcResult;
import src.controller.auth.authentication.AuthenticationController;
import src.core.config.AppConfig;
import src.core.config.SecurityConfig;
import src.core.security.JwtService;
import src.service.auth.AuthenticationService;
import src.service.external.EmailService;
import src.service.user.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Behavioral integration test for V06 — rate limiting.
 *
 * Runs in its own Spring context with app.rate-limit.enabled=true.
 * Uses a unique X-Forwarded-For IP (10.99.99.99) so the bucket is isolated
 * from any other test context and does not interfere with the rest of the suite.
 *
 * Kept separate from RateLimitingSecurityTest (structural/reflection tests)
 * because enabling rate limiting in the shared test context would exhaust the
 * bucket across unrelated test classes.
 */
@WebMvcTest(AuthenticationController.class)
@Import({SecurityConfig.class, AppConfig.class})
@ActiveProfiles("test")
@TestPropertySource(properties = "app.rate-limit.enabled=true")
@DisplayName("V06 – Rate limiting behavior (actual 429 verification)")
class RateLimitingBehaviorTest {

    // Each test method uses a different IP so bucket state does not bleed across methods
    // even if tests run in parallel within this class.
    private static final String IP_SIGNIN    = "10.99.1.1";
    private static final String IP_ISUSERTRUE = "10.99.1.2";
    private static final String IP_RETRYAFTER = "10.99.1.3";
    private static final int BURST = 15;

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;
    @MockBean private AuthenticationService authenticationService;
    @MockBean private EmailService emailService;

    @Test
    @DisplayName("After 10 rapid signin requests from the same IP, at least one must return 429")
    void rapidSignin_mustTrigger429AfterThreshold() throws Exception {
        List<Integer> statuses = new ArrayList<>();

        for (int i = 0; i < BURST; i++) {
            MvcResult result = mockMvc.perform(post("/api/v1/auth/signin")
                            .header("X-Forwarded-For", IP_SIGNIN)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"brute@force.com\",\"password\":\"attempt" + i + "\"}"))
                    .andReturn();
            statuses.add(result.getResponse().getStatus());
        }

        assertThat(statuses)
                .as("After %d rapid requests from IP %s, at least one must be rate-limited (429). "
                        + "Statuses received: %s", BURST, IP_SIGNIN, statuses)
                .contains(429);
    }

    @Test
    @DisplayName("After 10 rapid isUserTrue requests from the same IP, at least one must return 429")
    void rapidIsUserTrue_mustTrigger429AfterThreshold() throws Exception {
        List<Integer> statuses = new ArrayList<>();

        for (int i = 0; i < BURST; i++) {
            MvcResult result = mockMvc.perform(post("/api/v1/auth/isUserTrue")
                            .header("X-Forwarded-For", IP_ISUSERTRUE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"victim@example.com\",\"password\":\"guess" + i + "\"}"))
                    .andReturn();
            statuses.add(result.getResponse().getStatus());
        }

        assertThat(statuses)
                .as("isUserTrue is a brute-force oracle. After %d requests from IP %s, "
                        + "at least one must be 429. Statuses: %s", BURST, IP_ISUSERTRUE, statuses)
                .contains(429);
    }

    @Test
    @DisplayName("429 response must include Retry-After header")
    void rateLimited_response_mustIncludeRetryAfterHeader() throws Exception {
        String retryAfter = null;
        for (int i = 0; i < BURST; i++) {
            MvcResult result = mockMvc.perform(post("/api/v1/auth/signin")
                            .header("X-Forwarded-For", IP_RETRYAFTER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"test@test.com\",\"password\":\"pass" + i + "\"}"))
                    .andReturn();
            if (result.getResponse().getStatus() == 429) {
                retryAfter = result.getResponse().getHeader("Retry-After");
                break;
            }
        }

        assertThat(retryAfter)
                .as("429 response must include a Retry-After header so clients know when to retry")
                .isNotNull()
                .isNotEmpty();
    }
}
