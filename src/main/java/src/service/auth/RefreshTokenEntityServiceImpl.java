package src.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.repository.token.RefreshTokenEntity;
import src.repository.token.RefreshTokenRepository;
import src.repository.user.UserEntity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenEntityServiceImpl implements RefreshTokenEntityService {

    private final RefreshTokenRepository repository;

    @Override
    public RefreshTokenEntity store(UserEntity user, String rawToken, LocalDateTime expiresAt) {
        return repository.save(RefreshTokenEntity.builder()
                .tokenHash(hashToken(rawToken))
                .userId(user.getId())
                .issuedAt(LocalDateTime.now())
                .expiresAt(expiresAt)
                .revoked(false)
                .build());
    }

    @Override
    public Optional<RefreshTokenEntity> findByRawToken(String rawToken) {
        return repository.findByTokenHash(hashToken(rawToken));
    }

    @Override
    public void revoke(RefreshTokenEntity token) {
        token.setRevoked(true);
        repository.save(token);
    }

    @Override
    @Transactional
    public void revokeAllForUser(int userId) {
        repository.revokeAllByUserId(userId);
    }

    /**
     * SHA-256 hex digest of the raw token.
     * The token itself is never written to the database.
     */
    String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(64);
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is guaranteed by the Java spec — this branch is unreachable
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
