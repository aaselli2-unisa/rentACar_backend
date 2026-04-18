package src.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.controller.auth.authentication.request.SignInRequest;
import src.controller.auth.authentication.request.SignUpReqeust;
import src.controller.auth.token.request.RefreshTokenRequest;
import src.core.security.JwtService;
import src.core.security.model.JwtToken;
import src.repository.token.RefreshTokenEntity;
import src.repository.user.UserEntity;
import src.repository.user.UserEntityService;
import src.service.external.EmailService;
import src.service.user.customer.CustomerService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomAuthenticationServiceImpl implements AuthenticationService, AccessTokenService {
    private final CustomerService customerService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserEntityService userEntityService;
    private final EmailService emailService;
    // Security patch V10: server-side refresh token tracking
    private final RefreshTokenEntityService refreshTokenEntityService;

    @Override
    public void signUp(SignUpReqeust request) {
        // Security patch V02: public signup creates CUSTOMER accounts only.
        this.customerService.create(request.forCustomer());
        emailService.sendOtp(request.getEmailAddress());
    }

    @Override
    @Transactional
    public JwtToken signIn(SignInRequest request) {
        UserEntity userEntity = userEntityService.getByEmailAddress(request.getEmail());

        if (isUserTrue(request.getEmail(), request.getPassword())) {
            String accessToken = jwtService.generateToken(userEntity);

            // Security patch V10: issue a long-lived refresh token and persist its hash.
            String rawRefreshToken = jwtService.generateRefreshToken(userEntity);
            LocalDateTime expiresAt = jwtService.extractExpiration(rawRefreshToken)
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            refreshTokenEntityService.store(userEntity, rawRefreshToken, expiresAt);

            return JwtToken.builder()
                    .token(accessToken)
                    .refreshToken(rawRefreshToken)
                    .build();
        }
        throw new RuntimeException("Invalid credentials");
    }

    @Override
    @Transactional
    public JwtToken refreshToken(RefreshTokenRequest refreshTokenRequest) {
        // Security patch V10: validate the refresh token against the server-side store.
        String rawToken = refreshTokenRequest.getToken();
        Optional<RefreshTokenEntity> stored = refreshTokenEntityService.findByRawToken(rawToken);

        if (stored.isEmpty()) {
            throw new RuntimeException("Refresh token not found");
        }

        RefreshTokenEntity tokenRecord = stored.get();

        if (tokenRecord.isRevoked()) {
            // Possible token theft: an already-rotated token is being replayed.
            // Revoke all tokens for this user to force re-authentication.
            refreshTokenEntityService.revokeAllForUser(tokenRecord.getUserId());
            throw new RuntimeException("Refresh token has already been used");
        }

        if (tokenRecord.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token has expired");
        }

        // Rotate: revoke the consumed token and issue a new pair.
        refreshTokenEntityService.revoke(tokenRecord);

        UserEntity userEntity = userEntityService.getById(tokenRecord.getUserId());
        String newAccessToken = jwtService.generateToken(userEntity);
        String newRefreshToken = jwtService.generateRefreshToken(userEntity);
        LocalDateTime newExpiry = jwtService.extractExpiration(newRefreshToken)
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        refreshTokenEntityService.store(userEntity, newRefreshToken, newExpiry);

        return JwtToken.builder()
                .token(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Override
    @Transactional
    public boolean isUserTrue(String emailAddress, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(emailAddress, password)
            );
            // V-14: successful auth — reset failure counter
            recordSuccessfulLogin(emailAddress);
            return authentication.isAuthenticated();
        } catch (BadCredentialsException e) {
            // V-14: increment failure counter; lock after 5 attempts
            recordFailedLogin(emailAddress);
            throw e;
        }
        // LockedException propagates as-is — Spring Security handles the 401 response
    }

    // V-04: revoke all refresh tokens for the user (logout)
    @Override
    @Transactional
    public void logout(String email) {
        UserEntity user = userEntityService.getByEmailAddress(email);
        refreshTokenEntityService.revokeAllForUser(user.getId());
    }

    // V-14: track failed attempts; lock account for 15 minutes after 5 failures
    private void recordFailedLogin(String emailAddress) {
        try {
            UserEntity user = userEntityService.getByEmailAddress(emailAddress);
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            if (user.getFailedLoginAttempts() >= 5) {
                user.setLockedUntil(LocalDateTime.now().plusMinutes(15));
            }
            userEntityService.update(user);
        } catch (Exception ignored) {
            // Email not found — don't reveal existence via timing or error difference
        }
    }

    private void recordSuccessfulLogin(String emailAddress) {
        try {
            UserEntity user = userEntityService.getByEmailAddress(emailAddress);
            if (user.getFailedLoginAttempts() > 0 || user.getLockedUntil() != null) {
                user.setFailedLoginAttempts(0);
                user.setLockedUntil(null);
                userEntityService.update(user);
            }
        } catch (Exception ignored) {
            // Non-critical — don't fail login if reset fails
        }
    }
}