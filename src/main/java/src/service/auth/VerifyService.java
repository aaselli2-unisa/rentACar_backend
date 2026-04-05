package src.service.auth;

import src.core.security.model.JwtToken;

public interface VerifyService {

    JwtToken verifyEmailAddress(String token);

}
