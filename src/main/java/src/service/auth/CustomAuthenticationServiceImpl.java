package src.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
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
    public boolean isUserTrue(String emailAddress, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(emailAddress, password)
        );
        return authentication.isAuthenticated();
    }
}