package src.core.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Security patch V06 — rate limiting on authentication endpoints (OWASP A07 / CWE-307).
 *
 * Policy: 10 requests per minute per IP on /api/v1/auth/** and /api/v1/refresh-token.
 * Exceeding the limit returns HTTP 429 with a Retry-After: 60 header.
 *
 * Uses Bucket4j in-memory token-bucket algorithm (no external cache required).
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int CAPACITY = 10;
    private static final Duration REFILL_PERIOD = Duration.ofMinutes(1);

    @Value("${app.rate-limit.enabled:true}")
    private boolean enabled;

    // V-03: set of IPs we consider trusted proxies (loopback + RFC1918 private ranges)
    // Only these may set X-Forwarded-For. Override via app.rate-limit.trusted-proxies if needed.
    @Value("${app.rate-limit.trusted-proxy-cidrs:127.0.0.1,::1,10.,172.16.,192.168.}")
    private String trustedProxyCidrs;

    // V-09: Caffeine-backed bounded cache — evicts stale IP buckets after 2 min inactivity;
    // caps at 50 000 entries to prevent memory exhaustion under IP rotation attacks
    private final Cache<String, Bucket> buckets = Caffeine.newBuilder()
            .expireAfterAccess(2, TimeUnit.MINUTES)
            .maximumSize(50_000)
            .build();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith("/api/v1/auth/") && !path.startsWith("/api/v1/refresh-token");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!enabled) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = resolveClientIp(request);
        Bucket bucket = buckets.get(ip, this::newBucket);

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader("Retry-After", String.valueOf(REFILL_PERIOD.getSeconds()));
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"error\":\"Too many requests. Please wait before retrying.\"}");
        }
    }

    private Bucket newBucket(String ip) {
        Bandwidth limit = Bandwidth.classic(CAPACITY, Refill.greedy(CAPACITY, REFILL_PERIOD));
        return Bucket.builder().addLimit(limit).build();
    }

    // V-03: only trust X-Forwarded-For when the direct peer is a known trusted proxy;
    // otherwise an attacker can spoof the header to bypass per-IP rate limiting
    private String resolveClientIp(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        if (isTrustedProxy(remoteAddr)) {
            String forwarded = request.getHeader("X-Forwarded-For");
            if (forwarded != null && !forwarded.isBlank()) {
                return forwarded.split(",")[0].trim();
            }
        }
        return remoteAddr;
    }

    private boolean isTrustedProxy(String remoteAddr) {
        if (remoteAddr == null) return false;
        for (String cidr : trustedProxyCidrs.split(",")) {
            if (remoteAddr.startsWith(cidr.trim())) return true;
        }
        return false;
    }
}
