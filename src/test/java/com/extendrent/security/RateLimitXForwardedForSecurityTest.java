package com.extendrent.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import src.core.security.RateLimitFilter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * V-03 – Rate limiter must not blindly trust X-Forwarded-For (OWASP A07 / CWE-307).
 *
 * If X-Forwarded-For is accepted from any client, an attacker can rotate fake IP values
 * in each request to bypass the per-IP token-bucket limit entirely.
 *
 * Fix: resolveClientIp() only reads X-Forwarded-For when the direct TCP peer (remoteAddr)
 * is a trusted proxy (loopback or RFC1918 private range).
 */
@DisplayName("V-03 – Rate limiter X-Forwarded-For must only be trusted from known proxies")
class RateLimitXForwardedForSecurityTest {

    // @Value is only injected by Spring; for plain new RateLimitFilter() set via reflection
    private static final String DEFAULT_CIDRS = "127.0.0.1,::1,10.,172.16.,192.168.";

    private RateLimitFilter buildFilter() throws Exception {
        RateLimitFilter filter = new RateLimitFilter();
        Field cidrsField = RateLimitFilter.class.getDeclaredField("trustedProxyCidrs");
        cidrsField.setAccessible(true);
        cidrsField.set(filter, DEFAULT_CIDRS);
        return filter;
    }

    @Test
    @DisplayName("isTrustedProxy returns true for loopback address 127.0.0.1")
    void loopback_isTrustedProxy() throws Exception {
        RateLimitFilter filter = buildFilter();
        Method m = RateLimitFilter.class.getDeclaredMethod("isTrustedProxy", String.class);
        m.setAccessible(true);
        assertThat((boolean) m.invoke(filter, "127.0.0.1")).isTrue();
    }

    @Test
    @DisplayName("isTrustedProxy returns true for RFC1918 addresses (10.x, 172.16.x, 192.168.x)")
    void rfc1918_isTrustedProxy() throws Exception {
        RateLimitFilter filter = buildFilter();
        Method m = RateLimitFilter.class.getDeclaredMethod("isTrustedProxy", String.class);
        m.setAccessible(true);

        assertThat((boolean) m.invoke(filter, "10.0.0.1")).isTrue();
        assertThat((boolean) m.invoke(filter, "172.16.0.1")).isTrue();
        assertThat((boolean) m.invoke(filter, "192.168.1.1")).isTrue();
    }

    @Test
    @DisplayName("isTrustedProxy returns false for public IP addresses")
    void publicIp_isNotTrustedProxy() throws Exception {
        RateLimitFilter filter = buildFilter();
        Method m = RateLimitFilter.class.getDeclaredMethod("isTrustedProxy", String.class);
        m.setAccessible(true);

        assertThat((boolean) m.invoke(filter, "203.0.113.1")).isFalse();
        assertThat((boolean) m.invoke(filter, "8.8.8.8")).isFalse();
        assertThat((boolean) m.invoke(filter, "1.2.3.4")).isFalse();
    }

    @Test
    @DisplayName("resolveClientIp uses remoteAddr directly for public IP (ignores X-Forwarded-For)")
    void resolveClientIp_publicIp_ignoresForwardedHeader() throws Exception {
        RateLimitFilter filter = buildFilter();
        Method resolve = RateLimitFilter.class.getDeclaredMethod("resolveClientIp",
                jakarta.servlet.http.HttpServletRequest.class);
        resolve.setAccessible(true);

        org.springframework.mock.web.MockHttpServletRequest request =
                new org.springframework.mock.web.MockHttpServletRequest();
        request.setRemoteAddr("203.0.113.99");
        request.addHeader("X-Forwarded-For", "1.2.3.4");

        String resolvedIp = (String) resolve.invoke(filter, request);

        assertThat(resolvedIp)
                .as("When remoteAddr is public, X-Forwarded-For must be ignored; got '%s'", resolvedIp)
                .isEqualTo("203.0.113.99");
    }

    @Test
    @DisplayName("resolveClientIp uses X-Forwarded-For first IP when remoteAddr is a trusted proxy")
    void resolveClientIp_trustedProxy_usesForwardedHeader() throws Exception {
        RateLimitFilter filter = buildFilter();
        Method resolve = RateLimitFilter.class.getDeclaredMethod("resolveClientIp",
                jakarta.servlet.http.HttpServletRequest.class);
        resolve.setAccessible(true);

        org.springframework.mock.web.MockHttpServletRequest request =
                new org.springframework.mock.web.MockHttpServletRequest();
        request.setRemoteAddr("10.0.0.1");
        request.addHeader("X-Forwarded-For", "203.0.113.55, 10.0.0.1");

        String resolvedIp = (String) resolve.invoke(filter, request);

        assertThat(resolvedIp)
                .as("When remoteAddr is trusted proxy, return first IP from X-Forwarded-For")
                .isEqualTo("203.0.113.55");
    }

    @Test
    @DisplayName("RateLimitFilter uses Caffeine cache (not ConcurrentHashMap) for bounded memory")
    void rateLimitFilter_usesCaffeineCacheField() throws NoSuchFieldException {
        // V-09: the buckets field must be a Caffeine cache, not a raw ConcurrentHashMap
        java.lang.reflect.Field bucketsField = RateLimitFilter.class.getDeclaredField("buckets");
        bucketsField.setAccessible(true);
        assertThat(com.github.benmanes.caffeine.cache.Cache.class)
                .as("buckets field must be typed as Caffeine Cache for bounded TTL eviction")
                .isAssignableFrom(com.github.benmanes.caffeine.cache.Cache.class);
        // Verify the declared type is Cache, not ConcurrentHashMap
        assertThat(bucketsField.getType().getName())
                .as("buckets must not be ConcurrentHashMap (unbounded memory risk)")
                .doesNotContain("ConcurrentHashMap");
    }
}
