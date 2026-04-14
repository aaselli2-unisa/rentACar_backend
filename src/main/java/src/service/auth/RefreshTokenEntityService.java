package src.service.auth;

import src.repository.token.RefreshTokenEntity;
import src.repository.user.UserEntity;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Security patch V10 — server-side refresh token lifecycle management.
 *
 * Provides store / lookup / revocation so that refresh tokens can be invalidated
 * independently of their embedded JWT expiry claim (logout, password change,
 * token theft detection via rotation reuse-detection).
 */
public interface RefreshTokenEntityService {

    /** Store the hash of a newly issued refresh token. */
    RefreshTokenEntity store(UserEntity user, String rawToken, LocalDateTime expiresAt);

    /** Look up a stored token record by the raw (unhashed) token value. */
    Optional<RefreshTokenEntity> findByRawToken(String rawToken);

    /** Revoke a single token (used during rotation: invalidate the consumed token). */
    void revoke(RefreshTokenEntity token);

    /** Revoke all tokens for a user (logout, password change, account block). */
    void revokeAllForUser(int userId);
}
