package com.extendrent.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import src.core.security.JwtService;
import src.repository.user.UserEntity;
import src.service.user.model.DefaultUserStatus;
import src.service.user.model.UserRole;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * V-05 – JWT payload must not contain PII (OWASP A02 / CWE-312).
 *
 * JWT tokens are signed but NOT encrypted. The payload is Base64-encoded — readable
 * by anyone who intercepts the token without knowing the secret key. Storing name,
 * email, phone number, or any PII in the token leaks user data on every request
 * that passes through a proxy, CDN, or logging infrastructure.
 *
 * Fix: JwtService.generateToken() now stores only {id, role} and the standard
 * 'sub' claim (email address as the subject, required for token validation).
 */
@DisplayName("V-05 – JWT payload must not contain PII (name, surname, phone)")
class JwtClaimsPIISecurityTest {

    private static final String SECRET =
            "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long EXPIRATION_MS = 86_400_000L;

    private JwtService jwtService;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();
        inject("secretKey", SECRET);
        inject("expiration", EXPIRATION_MS);
        inject("refreshExpiration", EXPIRATION_MS * 7);
    }

    private void inject(String fieldName, Object value) throws Exception {
        Field f = JwtService.class.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(jwtService, value);
    }

    private UserEntity testUser() {
        UserEntity user = UserEntity.userBuilder()
                .emailAddress("alice@example.com")
                .name("Alice")
                .surname("Smith")
                .phoneNumber("5551234567")
                .password("$2a$10$hash")
                .authority(UserRole.CUSTOMER)
                .status(DefaultUserStatus.VERIFIED)
                .build();
        user.setIsDeleted(false);
        return user;
    }

    @Test
    @DisplayName("Generated access token does NOT contain firstname/name in payload")
    void token_doesNotContainFirstName() {
        String token = jwtService.generateToken(testUser());
        Claims claims = jwtService.extractAllClaims(token);

        assertThat(claims.containsKey("firstname"))
                .as("JWT payload must not include 'firstname' — PII must not be in Base64-readable token")
                .isFalse();
        assertThat(claims.containsKey("name"))
                .as("JWT payload must not include 'name'")
                .isFalse();
    }

    @Test
    @DisplayName("Generated access token does NOT contain lastname/surname in payload")
    void token_doesNotContainLastName() {
        String token = jwtService.generateToken(testUser());
        Claims claims = jwtService.extractAllClaims(token);

        assertThat(claims.containsKey("lastname"))
                .as("JWT payload must not include 'lastname'")
                .isFalse();
        assertThat(claims.containsKey("surname"))
                .as("JWT payload must not include 'surname'")
                .isFalse();
    }

    @Test
    @DisplayName("Generated access token does NOT contain phone number in payload")
    void token_doesNotContainPhoneNumber() {
        String token = jwtService.generateToken(testUser());
        Claims claims = jwtService.extractAllClaims(token);

        assertThat(claims.containsKey("phoneNumber"))
                .as("JWT payload must not include 'phoneNumber'")
                .isFalse();
    }

    @Test
    @DisplayName("Generated access token does NOT contain emailAddress claim (only standard 'sub')")
    void token_doesNotContainEmailAddressClaim() {
        String token = jwtService.generateToken(testUser());
        Claims claims = jwtService.extractAllClaims(token);

        assertThat(claims.containsKey("emailAddress"))
                .as("JWT payload must not duplicate email in a custom 'emailAddress' claim; "
                  + "email belongs in the standard 'sub' claim only")
                .isFalse();
    }

    @Test
    @DisplayName("Generated access token contains required claims: id and role")
    void token_containsRequiredClaims() {
        String token = jwtService.generateToken(testUser());
        Claims claims = jwtService.extractAllClaims(token);

        assertThat(claims.containsKey("id"))
                .as("JWT must contain 'id' claim for server-side lookups")
                .isTrue();
        assertThat(claims.containsKey("role"))
                .as("JWT must contain 'role' claim for authorization decisions")
                .isTrue();
        assertThat(claims.getSubject())
                .as("JWT 'sub' must be the user's email for authentication")
                .isEqualTo("alice@example.com");
    }
}
