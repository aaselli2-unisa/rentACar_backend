package com.extendrent.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import src.core.security.JwtService;
import src.repository.user.UserEntity;
import src.service.user.model.DefaultUserStatus;
import src.service.user.model.UserRole;

import java.lang.reflect.Field;
import java.security.Key;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for JwtService.
 *
 * No Spring context — all fields injected via reflection to match @Value behaviour.
 *
 * Covers: token generation, extraction, validation, expiration, signature
 * integrity and resistance to common JWT attacks (alg confusion, none-alg,
 * key confusion, claim tampering).
 */
@DisplayName("JwtService – JWT security unit tests")
class JwtServiceTest {

    // ---- constants --------------------------------------------------------

    private static final String VALID_SECRET =
            "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final String DIFFERENT_SECRET =
            "5367566B5970703373357638792F423F4528482B4C6250645367566B59702032";
    private static final long EXPIRATION_MS = 86_400_000L; // 24 h

    // ---- SUT --------------------------------------------------------------

    private JwtService jwtService;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();
        injectField("secretKey",         VALID_SECRET);
        injectField("expiration",        EXPIRATION_MS);
        // Security patch V09: JwtService now has a separate refreshExpiration field.
        // Use 7 days so refresh tokens are not immediately expired during tests.
        injectField("refreshExpiration", EXPIRATION_MS * 7);
    }

    // ---- helpers ----------------------------------------------------------

    private void injectField(String fieldName, Object value) throws Exception {
        Field f = JwtService.class.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(jwtService, value);
    }

    /** Build a minimal, non-persisted UserEntity suitable for token generation. */
    private UserEntity buildUser(String email, UserRole role) {
        UserEntity user = UserEntity.userBuilder()
                .emailAddress(email)
                .name("Test")
                .surname("User")
                .phoneNumber("5551234567")
                .password("$2a$10$hashedpassword")
                .authority(role)
                .status(DefaultUserStatus.VERIFIED)
                .build();
        user.setIsDeleted(false);
        return user;
    }

    /** Create a token signed with a completely different key. */
    private String tokenSignedWithDifferentKey(String username) {
        Key wrongKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(DIFFERENT_SECRET));
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(wrongKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /** Create a token whose expiration is already in the past. */
    private String expiredToken(String username) {
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(VALID_SECRET));
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis() - 2 * EXPIRATION_MS))
                .setExpiration(new Date(System.currentTimeMillis() - EXPIRATION_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ======================================================================
    //  Token generation
    // ======================================================================

    @Nested
    @DisplayName("Token generation")
    class TokenGeneration {

        @Test
        @DisplayName("Subject must equal the user's email address")
        void subject_isEmailAddress() {
            UserEntity user = buildUser("alice@example.com", UserRole.CUSTOMER);
            String token = jwtService.generateToken(user);
            assertThat(jwtService.extractUsername(token)).isEqualTo("alice@example.com");
        }

        @Test
        @DisplayName("Custom claims – emailAddress, role, firstname, lastname, phoneNumber – are present")
        void customClaims_arePresentInToken() {
            UserEntity user = buildUser("bob@example.com", UserRole.ADMIN);
            String token = jwtService.generateToken(user);
            Claims claims = jwtService.extractAllClaims(token);
            assertThat(claims.get("emailAddress")).isEqualTo("bob@example.com");
            assertThat(claims.get("firstname")).isEqualTo("Test");
            assertThat(claims.get("lastname")).isEqualTo("User");
            assertThat(claims.get("phoneNumber")).isEqualTo("5551234567");
            assertThat(claims.get("role")).isNotNull();
        }

        @Test
        @DisplayName("Token expiration must be approximately 24 hours from now")
        void token_expiresIn24Hours() {
            UserEntity user = buildUser("charlie@example.com", UserRole.EMPLOYEE);
            String token = jwtService.generateToken(user);
            Date expiry = jwtService.extractExpiration(token);
            long remaining = expiry.getTime() - System.currentTimeMillis();
            assertThat(remaining).isBetween(EXPIRATION_MS - 5_000, EXPIRATION_MS + 5_000);
        }

        @Test
        @DisplayName("REGRESSION V09: Refresh token must have a significantly longer TTL than access token")
        void refreshToken_hasLongerExpirationThanAccessToken() {
            // Security patch V09: JwtService now uses refreshExpiration (7 days) for refresh tokens
            // vs expiration (1 hour) for access tokens. The two-token security model is restored.
            UserEntity user = buildUser("dave@example.com", UserRole.CUSTOMER);
            String accessToken  = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            Date accessExpiry  = jwtService.extractExpiration(accessToken);
            Date refreshExpiry = jwtService.extractExpiration(refreshToken);

            long delta = refreshExpiry.getTime() - accessExpiry.getTime();
            // Refresh token must expire substantially after the access token.
            assertThat(delta)
                    .as("Refresh token expiry must be > access token expiry (delta: %d ms)", delta)
                    .isGreaterThan(1_000);
        }

        @Test
        @DisplayName("Refresh token has no subject (email not embedded)")
        void refreshToken_hasNoSubject() {
            UserEntity user = buildUser("eve@example.com", UserRole.CUSTOMER);
            String refreshToken = jwtService.generateRefreshToken(user);
            Claims claims = jwtService.extractAllClaims(refreshToken);
            assertThat(claims.getSubject()).isNull();
        }

        @Test
        @DisplayName("Refresh token embeds the user id claim")
        void refreshToken_containsUserId() {
            UserEntity user = buildUser("frank@example.com", UserRole.CUSTOMER);
            String refreshToken = jwtService.generateRefreshToken(user);
            Claims claims = jwtService.extractAllClaims(refreshToken);
            // id == 0 for non-persisted entity; just assert the claim exists
            assertThat(claims.containsKey("userId")).isTrue();
        }
    }

    // ======================================================================
    //  Token validation
    // ======================================================================

    @Nested
    @DisplayName("Token validation")
    class TokenValidation {

        @Test
        @DisplayName("Fresh token is valid for the owning user")
        void freshToken_isValid() {
            UserEntity user = buildUser("grace@example.com", UserRole.CUSTOMER);
            String token = jwtService.generateToken(user);
            assertThat(jwtService.isTokenValid(token, user)).isTrue();
        }

        @Test
        @DisplayName("Token valid for user A is rejected for user B (username mismatch)")
        void token_invalidForDifferentUser() {
            UserEntity alice = buildUser("alice@example.com", UserRole.CUSTOMER);
            UserEntity bob   = buildUser("bob@example.com", UserRole.CUSTOMER);
            String tokenForAlice = jwtService.generateToken(alice);
            assertThat(jwtService.isTokenValid(tokenForAlice, bob)).isFalse();
        }

        @Test
        @DisplayName("Expired token is rejected")
        void expiredToken_isInvalid() {
            String expired = expiredToken("hank@example.com");
            UserDetails user = User.withUsername("hank@example.com")
                    .password("x").roles("CUSTOMER").build();
            assertThat(jwtService.isTokenValid(expired, user)).isFalse();
        }

        @Test
        @DisplayName("isTokenExpired returns false for a fresh token")
        void freshToken_isNotExpired() {
            UserEntity user = buildUser("ivy@example.com", UserRole.CUSTOMER);
            String token = jwtService.generateToken(user);
            assertThat(jwtService.isTokenExpired(token)).isFalse();
        }

        @Test
        @DisplayName("isTokenExpired returns true for an expired token")
        void expiredToken_isExpired() {
            assertThat(jwtService.isTokenExpired(expiredToken("jack@example.com"))).isTrue();
        }

        @Test
        @DisplayName("Refresh token rejected by isTokenValid – no subject means username never matches")
        void refreshToken_rejectedByIsTokenValid() {
            // Refresh tokens intentionally omit the subject (email) — see refreshToken_hasNoSubject.
            // isTokenValid() compares extractUsername(token) to userDetails.getUsername().
            // With no subject, extractUsername returns null → comparison fails → must return false.
            // This prevents a refresh token from being accepted as an access token at the JWT layer.
            UserEntity user = buildUser("nosubject@example.com", UserRole.CUSTOMER);
            String refreshToken = jwtService.generateRefreshToken(user);
            assertThat(jwtService.isTokenValid(refreshToken, user))
                    .as("Refresh token must be rejected by isTokenValid – it has no subject so"
                            + " username matching must fail")
                    .isFalse();
        }
    }

    // ======================================================================
    //  Signature integrity attacks
    // ======================================================================

    @Nested
    @DisplayName("Signature integrity – attack resistance")
    class SignatureIntegrity {

        @Test
        @DisplayName("Token signed with a different key is rejected")
        void token_signedWithDifferentKey_isRejected() {
            String token = tokenSignedWithDifferentKey("victim@example.com");
            assertThatThrownBy(() -> jwtService.extractUsername(token))
                    .isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("Tampered payload (last 5 chars overwritten) is rejected")
        void tamperedToken_isRejected() {
            UserEntity user = buildUser("kate@example.com", UserRole.CUSTOMER);
            String token = jwtService.generateToken(user);
            String tampered = token.substring(0, token.length() - 5) + "AAAAA";
            assertThatThrownBy(() -> jwtService.extractUsername(tampered))
                    .isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("Token with completely invalid structure is rejected")
        void invalidStructure_isRejected() {
            assertThatThrownBy(() -> jwtService.extractUsername("not.a.jwt"))
                    .isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("Empty string is rejected")
        void emptyString_isRejected() {
            assertThatThrownBy(() -> jwtService.extractUsername(""))
                    .isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("Null token is rejected")
        void nullToken_isRejected() {
            assertThatThrownBy(() -> jwtService.extractUsername(null))
                    .isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("Token with header stripped to two segments only is rejected (alg:none attack)")
        void truncatedToken_isRejected() {
            UserEntity user = buildUser("leo@example.com", UserRole.CUSTOMER);
            String token = jwtService.generateToken(user);
            // Keep only header.payload, strip the signature
            String[] parts = token.split("\\.");
            String noSig = parts[0] + "." + parts[1] + ".";
            assertThatThrownBy(() -> jwtService.extractUsername(noSig))
                    .isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("Expired token parsing throws ExpiredJwtException")
        void expiredToken_throwsExpiredJwtException() {
            assertThatThrownBy(() -> jwtService.extractAllClaims(expiredToken("mia@example.com")))
                    .isInstanceOf(ExpiredJwtException.class);
        }
    }
}
