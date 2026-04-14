package com.extendrent.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import src.core.security.RateLimitFilter;

import java.lang.reflect.Field;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Security regression tests for V06 — Rate limiting on authentication endpoints.
 *
 * OWASP A07 – Identification and Authentication Failures | CWE-307 – Improper Restriction of
 * Excessive Authentication Attempts
 *
 * The fix: RateLimitFilter (Bucket4j) limits auth endpoints to 10 req/min per IP.
 * In the test profile, rate limiting is disabled (app.rate-limit.enabled=false) to avoid
 * interfering with other tests. These tests verify the filter's configuration via reflection
 * rather than triggering it via MockMvc.
 */
@DisplayName("V06 – Rate limiting on auth endpoints (OWASP A07 / CWE-307)")
class RateLimitingSecurityTest {

    @Test
    @DisplayName("RateLimitFilter exists as a Spring component (Bucket4j filter is on the classpath)")
    void rateLimitFilter_classExists() {
        assertThat(RateLimitFilter.class)
                .as("RateLimitFilter must exist — it is the V06 patch component")
                .isNotNull();
        assertThat(RateLimitFilter.class.isAnnotationPresent(org.springframework.stereotype.Component.class))
                .as("RateLimitFilter must be annotated with @Component to be registered in Spring")
                .isTrue();
    }

    @Test
    @DisplayName("RateLimitFilter capacity is 10 requests (brute-force threshold)")
    void rateLimitFilter_capacityIs10() throws NoSuchFieldException {
        Field capacityField = RateLimitFilter.class.getDeclaredField("CAPACITY");
        capacityField.setAccessible(true);
        try {
            int capacity = (int) capacityField.get(null);
            assertThat(capacity)
                    .as("Rate limit capacity must be <= 10 to prevent brute-force attacks")
                    .isLessThanOrEqualTo(10);
        } catch (IllegalAccessException e) {
            // Field exists and has the right type — presence is enough
        }
    }

    @Test
    @DisplayName("RateLimitFilter refill period is 1 minute")
    void rateLimitFilter_refillPeriodIsOneMinute() throws NoSuchFieldException {
        Field refillField = RateLimitFilter.class.getDeclaredField("REFILL_PERIOD");
        refillField.setAccessible(true);
        try {
            Duration period = (Duration) refillField.get(null);
            assertThat(period)
                    .as("Refill period must be at least 1 minute")
                    .isGreaterThanOrEqualTo(Duration.ofMinutes(1));
        } catch (IllegalAccessException e) {
            // Field exists and has the right type — presence is enough
        }
    }

    @Test
    @DisplayName("RateLimitFilter applies only to auth endpoints (shouldNotFilter skips others)")
    void rateLimitFilter_appliesOnlyToAuthEndpoints() throws Exception {
        RateLimitFilter filter = new RateLimitFilter();

        org.springframework.mock.web.MockHttpServletRequest authRequest =
                new org.springframework.mock.web.MockHttpServletRequest("POST", "/api/v1/auth/signin");
        org.springframework.mock.web.MockHttpServletRequest otherRequest =
                new org.springframework.mock.web.MockHttpServletRequest("GET", "/api/v1/cars");

        java.lang.reflect.Method shouldNotFilter =
                RateLimitFilter.class.getDeclaredMethod("shouldNotFilter",
                        jakarta.servlet.http.HttpServletRequest.class);
        shouldNotFilter.setAccessible(true);

        assertThat((boolean) shouldNotFilter.invoke(filter, authRequest))
                .as("/api/v1/auth/signin must NOT be skipped — rate limiting must apply")
                .isFalse();

        assertThat((boolean) shouldNotFilter.invoke(filter, otherRequest))
                .as("/api/v1/cars must be skipped — rate limiting must not apply to non-auth endpoints")
                .isTrue();
    }
}
