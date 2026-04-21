package com.extendrent.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import src.core.security.JwtService;
import src.repository.user.UserEntity;
import src.service.user.model.DefaultUserStatus;
import src.service.user.model.UserRole;

import java.lang.reflect.Field;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Security regression test for V09 — Refresh token and access token share the same TTL.
 *
 * OWASP A07 – Identification and Authentication Failures | CWE-613 – Insufficient Session Expiration
 *
 * JwtService.generateRefreshToken() uses the same {@code expiration} value (86400000 ms = 24h)
 * as generateToken(). The two-token security model (short-lived access + long-lived refresh) is
 * therefore pointless: stealing either token grants the same 24h window with no revocation.
 *
 * THIS TEST PASSES — it became green after V09 was patched: a separate {@code refresh-expiration}
 * property (e.g. 7–30 days) is introduced and used in generateRefreshToken().
 */
@DisplayName("V09 – Access and refresh tokens share the same TTL (OWASP A07 / CWE-613)")
class JwtTtlSecurityTest {

    private static final String SECRET =
            "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long ACCESS_EXPIRATION_MS  = 3_600_000L;       // 1 h (after V09 patch)
    private static final long REFRESH_EXPIRATION_MS = 604_800_000L;     // 7 days

    private JwtService jwtService;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();
        setField("secretKey",         SECRET);
        setField("expiration",        ACCESS_EXPIRATION_MS);
        setField("refreshExpiration", REFRESH_EXPIRATION_MS);
    }

    private void setField(String name, Object value) throws Exception {
        Field f = JwtService.class.getDeclaredField(name);
        f.setAccessible(true);
        f.set(jwtService, value);
    }

    private UserEntity testUser() {
        UserEntity u = UserEntity.userBuilder()
                .emailAddress("test@example.com")
                .name("Test").surname("User")
                .phoneNumber("5551234567")
                .password("$2a$10$hash")
                .authority(UserRole.CUSTOMER)
                .status(DefaultUserStatus.VERIFIED)
                .build();
        u.setIsDeleted(false);
        return u;
    }

    @Test
    @DisplayName("PATCHED V09: refresh token TTL must be at least 7× longer than access token TTL")
    void refreshToken_mustHaveSignificantlyLongerTtl_than_accessToken() {
        UserEntity user = testUser();

        Date accessExpiry  = jwtService.extractExpiration(jwtService.generateToken(user));
        Date refreshExpiry = jwtService.extractExpiration(jwtService.generateRefreshToken(user));

        long accessTtlMs  = accessExpiry.getTime()  - System.currentTimeMillis();
        long refreshTtlMs = refreshExpiry.getTime() - System.currentTimeMillis();

        assertThat(refreshTtlMs)
                .as("Refresh token TTL (%d ms) must be at least 7× the access token TTL (%d ms). "
                    + "Currently both use the same %d ms window.", refreshTtlMs, accessTtlMs, ACCESS_EXPIRATION_MS)
                .isGreaterThan(accessTtlMs * 7);
    }

    @Test
    @DisplayName("PATCHED V09: access token TTL is ≤ 1 hour (reduced from 24 hours)")
    void accessToken_ttlMustNotExceedOneHour() {
        UserEntity user = testUser();

        Date accessExpiry = jwtService.extractExpiration(jwtService.generateToken(user));
        long accessTtlMs  = accessExpiry.getTime() - System.currentTimeMillis();

        long oneHourMs = 3_600_000L;

        assertThat(accessTtlMs)
                .as("Access token TTL (%d ms = %.1f hours) must be ≤ 1 hour (3600000 ms) "
                    + "to limit the damage from a stolen token. Current value: 24 hours.",
                    accessTtlMs, accessTtlMs / 3_600_000.0)
                .isLessThanOrEqualTo(oneHourMs);
    }

    @Test
    @DisplayName("REGRESSION: refresh token expiry must be substantially larger than access token expiry")
    void refreshToken_expiryMustBeSubstantiallyLargerThanAccessToken() {
        UserEntity user = testUser();

        Date accessExpiry  = jwtService.extractExpiration(jwtService.generateToken(user));
        Date refreshExpiry = jwtService.extractExpiration(jwtService.generateRefreshToken(user));

        long delta = refreshExpiry.getTime() - accessExpiry.getTime();
        // After V09 patch: refresh token expires 7 days later than access token
        assertThat(delta)
                .as("Refresh token must expire significantly after access token (delta was %d ms)", delta)
                .isGreaterThan(1_000);
    }
}
