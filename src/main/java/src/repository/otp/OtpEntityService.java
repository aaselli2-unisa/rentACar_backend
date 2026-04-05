package src.repository.otp;

public interface OtpEntityService {

    void createOtp(OtpEntity otpEntity);

    OtpEntity getByVerificationToken(String token);

}
