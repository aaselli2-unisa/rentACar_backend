package com.extendrent.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import src.core.security.JwtService;
import src.repository.user.UserEntity;
import src.service.user.UserService;
import src.service.user.model.DefaultUserStatus;
import src.service.user.model.UserRole;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Shared test utilities for security-layer MockMvc tests.
 *
 * Provides helpers to:
 * - Build minimal UserEntity instances without persistence
 * - Generate valid and invalid JWT strings against the test secret
 * - Configure JwtService / UserService mocks so the JwtAuthFilter lets requests through
 */
public final class SecurityTestSupport {

    public static final String JWT_SECRET =
            "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    public static final long JWT_EXPIRATION_MS = 86_400_000L;

    // Sentinel value used as Bearer token in MockMvc requests
    public static final String VALID_TOKEN_SENTINEL  = "mock-valid-token";
    public static final String INVALID_TOKEN_SENTINEL = "mock-invalid-token";
    public static final String EXPIRED_TOKEN_SENTINEL = "mock-expired-token";

    private SecurityTestSupport() {}

    // -----------------------------------------------------------------------
    //  Entity builders
    // -----------------------------------------------------------------------

    /** Build a non-persisted UserEntity for use in mock configuration. */
    public static UserEntity userEntity(String email, UserRole role) {
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

    // -----------------------------------------------------------------------
    //  JWT string builders (bypassing JwtService to test the filter directly)
    // -----------------------------------------------------------------------

    public static String validJwt(String email, UserRole role) {
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET));
        Map<String, Object> claims = new HashMap<>();
        claims.put("emailAddress", email);
        claims.put("role", role.name());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public static String expiredJwt(String email) {
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET));
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis() - 2 * JWT_EXPIRATION_MS))
                .setExpiration(new Date(System.currentTimeMillis() - JWT_EXPIRATION_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // -----------------------------------------------------------------------
    //  Mock configuration helpers
    // -----------------------------------------------------------------------

    /**
     * Configure the JwtService and UserService mocks so that a request carrying
     * the given {@code token} string in the Authorization header will be treated
     * as authenticated with the supplied {@code user}.
     */
    public static void setupAuthMocks(JwtService jwtService,
                                      UserService userService,
                                      String token,
                                      UserEntity user) {
        when(jwtService.extractUsername(token)).thenReturn(user.getEmailAddress());
        when(jwtService.isTokenValid(eq(token), any(UserDetails.class))).thenReturn(true);
        when(userService.loadUserByUsername(user.getEmailAddress())).thenReturn(user);
    }

    /**
     * Configure the mocks so that any unknown token is treated as invalid
     * (extractUsername returns null).
     */
    public static void setupInvalidTokenMock(JwtService jwtService, String token) {
        when(jwtService.extractUsername(token)).thenReturn(null);
    }
}
