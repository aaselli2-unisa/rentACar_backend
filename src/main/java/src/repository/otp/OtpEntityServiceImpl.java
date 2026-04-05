package src.repository.otp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import src.core.exception.DataNotFoundException;

import static src.core.exception.type.NotFoundExceptionType.OTP_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class OtpEntityServiceImpl implements OtpEntityService {
    private final OtpRepository otpRepository;

    @Override
    public void createOtp(OtpEntity otpEntity) {
        otpRepository.save(otpEntity);
    }

    @Override
    public OtpEntity getByVerificationToken(String token) {
        return otpRepository.findByVerificationToken(token)
                .orElseThrow(() -> new DataNotFoundException(OTP_NOT_FOUND));
    }
}
