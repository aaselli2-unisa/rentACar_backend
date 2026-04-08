package src.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.core.security.JwtService;
import src.core.security.model.JwtToken;
import src.repository.otp.OtpEntity;
import src.repository.otp.OtpEntityServiceImpl;
import src.repository.user.UserEntity;
import src.repository.user.UserEntityService;

import static src.service.user.model.DefaultUserStatus.VERIFIED;

@Service
@RequiredArgsConstructor
public class VerifyServiceImpl implements VerifyService {

    private final OtpEntityServiceImpl otpEntityService;
    private final UserEntityService userEntityService;
    private final JwtService jwtService;

    @Override
    @Transactional
    public JwtToken verifyEmailAddress(String token) {
        OtpEntity otp = otpEntityService.getByVerificationToken(token);
        UserEntity user = userEntityService.getByEmailAddress(otp.getDestination());
        user.setStatus(VERIFIED);
        userEntityService.update(user);
        return JwtToken.builder().token(jwtService.generateToken(user)).build();
    }
}
