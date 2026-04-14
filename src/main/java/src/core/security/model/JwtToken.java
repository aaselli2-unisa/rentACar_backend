package src.core.security.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtToken {
    /** Short-lived access token (1 hour). */
    String token;

    /**
     * Security patch V10: long-lived refresh token (7 days), included in sign-in response
     * and rotation responses. Null in responses that only emit a new access token.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String refreshToken;
}
