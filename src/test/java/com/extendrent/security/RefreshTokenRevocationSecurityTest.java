package com.extendrent.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import src.controller.auth.authentication.request.SignInRequest;
import src.controller.auth.token.request.RefreshTokenRequest;
import src.core.security.JwtService;
import src.core.security.model.JwtToken;
import src.repository.token.RefreshTokenEntity;
import src.repository.user.UserEntity;
import src.repository.user.UserEntityService;
import src.service.auth.CustomAuthenticationServiceImpl;
import src.service.auth.RefreshTokenEntityService;
import src.service.external.EmailService;
import src.service.user.customer.CustomerService;
import src.service.user.model.DefaultUserStatus;
import src.service.user.model.UserRole;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Security regression tests for V10 — Refresh token revocation / token rotation.
 *
 * OWASP A07 – Identification and Authentication Failures | CWE-613 – Insufficient Session Expiration
 *
 * Security patch V10 adds server-side tracking so that:
 * 1. Each refresh token is single-use (rotation: old token revoked on use).
 * 2. Replaying a revoked token is detected and all user tokens are revoked (theft detection).
 * 3. Expired tokens are rejected regardless of JWT signature validity.
 */
@DisplayName("V10 – Refresh token revocation and rotation (OWASP A07 / CWE-613)")
class RefreshTokenRevocationSecurityTest {

    private JwtService jwtService;
    private UserEntityService userEntityService;
    private RefreshTokenEntityService refreshTokenEntityService;
    private CustomerService customerService;
    private EmailService emailService;

    private CustomAuthenticationServiceImpl service;

    private static final String RAW_REFRESH_TOKEN = "eyJhbGciOiJIUzI1NiJ9.REFRESH.SIG";
    private static final String NEW_REFRESH_TOKEN  = "eyJhbGciOiJIUzI1NiJ9.NEW_REFRESH.SIG2";
    private static final String EMAIL = "user@example.com";

    @BeforeEach
    void setUp() {
        jwtService               = mock(JwtService.class);
        userEntityService        = mock(UserEntityService.class);
        refreshTokenEntityService = mock(RefreshTokenEntityService.class);
        customerService          = mock(CustomerService.class);
        emailService             = mock(EmailService.class);

        service = new CustomAuthenticationServiceImpl(
                customerService, jwtService, null /* authManager not needed here */,
                userEntityService, emailService, refreshTokenEntityService
        );
    }

    private UserEntity testUser() {
        UserEntity u = UserEntity.userBuilder()
                .emailAddress(EMAIL)
                .name("Test").surname("User")
                .phoneNumber("5551234567")
                .password("$2a$10$hash")
                .authority(UserRole.CUSTOMER)
                .status(DefaultUserStatus.VERIFIED)
                .build();
        u.setIsDeleted(false);
        return u;
    }

    private RefreshTokenEntity validStoredToken(int userId) {
        return RefreshTokenEntity.builder()
                .id(1)
                .tokenHash("some-hash")
                .userId(userId)
                .issuedAt(LocalDateTime.now().minusMinutes(5))
                .expiresAt(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();
    }

    // -------------------------------------------------------------------------
    //  Happy path: first use of a valid token should succeed and rotate
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Valid refresh token returns new access + refresh token pair")
    void validRefreshToken_returnsNewTokenPair() {
        UserEntity user = testUser();
        RefreshTokenEntity stored = validStoredToken(user.getId());

        when(refreshTokenEntityService.findByRawToken(RAW_REFRESH_TOKEN))
                .thenReturn(Optional.of(stored));
        when(userEntityService.getById(user.getId())).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn(NEW_REFRESH_TOKEN);
        when(jwtService.extractExpiration(NEW_REFRESH_TOKEN))
                .thenReturn(new Date(System.currentTimeMillis() + 604_800_000L));

        JwtToken result = service.refreshToken(new RefreshTokenRequest(EMAIL, RAW_REFRESH_TOKEN));

        assertThat(result.getToken()).isEqualTo("new-access-token");
        assertThat(result.getRefreshToken()).isEqualTo(NEW_REFRESH_TOKEN);
    }

    @Test
    @DisplayName("After rotation, the consumed refresh token is revoked")
    void afterRotation_oldRefreshToken_isRevoked() {
        UserEntity user = testUser();
        RefreshTokenEntity stored = validStoredToken(user.getId());

        when(refreshTokenEntityService.findByRawToken(RAW_REFRESH_TOKEN))
                .thenReturn(Optional.of(stored));
        when(userEntityService.getById(user.getId())).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn(NEW_REFRESH_TOKEN);
        when(jwtService.extractExpiration(NEW_REFRESH_TOKEN))
                .thenReturn(new Date(System.currentTimeMillis() + 604_800_000L));

        service.refreshToken(new RefreshTokenRequest(EMAIL, RAW_REFRESH_TOKEN));

        // The old token record must be revoked
        verify(refreshTokenEntityService).revoke(stored);
        // A new token record must be stored
        verify(refreshTokenEntityService).store(eq(user), eq(NEW_REFRESH_TOKEN), any(LocalDateTime.class));
    }

    // -------------------------------------------------------------------------
    //  Revoked token replay — theft detection
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Replaying an already-revoked token is rejected and all user tokens are invalidated")
    void revokedToken_isRejected_andAllUserTokensRevoked() {
        UserEntity user = testUser();
        RefreshTokenEntity revokedToken = RefreshTokenEntity.builder()
                .id(1)
                .tokenHash("some-hash")
                .userId(user.getId())
                .issuedAt(LocalDateTime.now().minusDays(1))
                .expiresAt(LocalDateTime.now().plusDays(6))
                .revoked(true)   // ← already used
                .build();

        when(refreshTokenEntityService.findByRawToken(RAW_REFRESH_TOKEN))
                .thenReturn(Optional.of(revokedToken));

        assertThatThrownBy(() ->
                service.refreshToken(new RefreshTokenRequest(EMAIL, RAW_REFRESH_TOKEN)))
                .as("Replaying a revoked refresh token must be rejected")
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already been used");

        // Theft-detection: all tokens for this user are revoked
        verify(refreshTokenEntityService).revokeAllForUser(user.getId());
    }

    // -------------------------------------------------------------------------
    //  Unknown token
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Valid JWT signature but absent server-side record is rejected (store is authoritative)")
    void validJwtSignatureButAbsentServerRecord_isRejected() {
        // A JWT-shaped token that is cryptographically plausible but has no entry in the
        // server-side store must still be rejected.  The server-side store is the single
        // source of truth for refresh token validity — the JWT signature alone is not enough.
        when(refreshTokenEntityService.findByRawToken(anyString()))
                .thenReturn(Optional.empty());

        // Use a token string that looks like a real JWT (header.payload.signature)
        String plausibleJwt = "eyJhbGciOiJIUzI1NiJ9"
                + ".eyJ1c2VySWQiOjEsImlhdCI6MTcwMDAwMDAwMH0"
                + ".validLookingSignatureButNotInStore";

        assertThatThrownBy(() ->
                service.refreshToken(new RefreshTokenRequest(EMAIL, plausibleJwt)))
                .as("Even a JWT-shaped token must be rejected when it is absent from the server-side"
                        + " store — the store is authoritative, not the JWT signature")
                .isInstanceOf(RuntimeException.class);
    }

    // -------------------------------------------------------------------------
    //  Expired token
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Server-side expired refresh token is rejected even if JWT signature is valid")
    void expiredRefreshToken_isRejected() {
        UserEntity user = testUser();
        RefreshTokenEntity expiredToken = RefreshTokenEntity.builder()
                .id(1)
                .tokenHash("some-hash")
                .userId(user.getId())
                .issuedAt(LocalDateTime.now().minusDays(8))
                .expiresAt(LocalDateTime.now().minusSeconds(1))  // ← expired
                .revoked(false)
                .build();

        when(refreshTokenEntityService.findByRawToken(RAW_REFRESH_TOKEN))
                .thenReturn(Optional.of(expiredToken));

        assertThatThrownBy(() ->
                service.refreshToken(new RefreshTokenRequest(EMAIL, RAW_REFRESH_TOKEN)))
                .as("A server-side expired token must be rejected")
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("expired");
    }

    // -------------------------------------------------------------------------
    //  Sign-in stores the refresh token
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Sign-in persists the refresh token hash in the server-side store")
    void signIn_storesRefreshTokenHash() throws Exception {
        // Re-wire with a real AuthenticationManager mock
        var authManager = mock(org.springframework.security.authentication.AuthenticationManager.class);
        var auth = mock(org.springframework.security.core.Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(authManager.authenticate(any())).thenReturn(auth);

        service = new CustomAuthenticationServiceImpl(
                customerService, jwtService, authManager,
                userEntityService, emailService, refreshTokenEntityService
        );

        UserEntity user = testUser();
        when(userEntityService.getByEmailAddress(EMAIL)).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn(RAW_REFRESH_TOKEN);
        when(jwtService.extractExpiration(RAW_REFRESH_TOKEN))
                .thenReturn(new Date(System.currentTimeMillis() + 604_800_000L));

        JwtToken result = service.signIn(new SignInRequest(EMAIL, "password"));

        assertThat(result.getToken()).isEqualTo("access-token");
        assertThat(result.getRefreshToken()).isEqualTo(RAW_REFRESH_TOKEN);

        // Refresh token hash must be persisted
        verify(refreshTokenEntityService).store(eq(user), eq(RAW_REFRESH_TOKEN), any(LocalDateTime.class));
    }
}
