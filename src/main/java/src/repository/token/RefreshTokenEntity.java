package src.repository.token;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Security patch V10 — server-side refresh token tracking.
 *
 * Stores the SHA-256 hash (never the raw value) of every issued refresh token
 * so that the application can validate, rotate, and revoke tokens independently
 * of their embedded JWT expiry claim.
 *
 * Table: refresh_tokens
 * Created automatically via spring.jpa.hibernate.ddl-auto=update.
 */
@Entity
@Table(name = "refresh_tokens", indexes = {
        @Index(name = "idx_refresh_token_hash", columnList = "token_hash"),
        @Index(name = "idx_refresh_token_user_id", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /** SHA-256 hex digest of the raw JWT refresh token — never stored in plain text. */
    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /** True if this token was revoked (used once, logout, or password change). */
    @Column(name = "revoked", nullable = false)
    private boolean revoked;
}
