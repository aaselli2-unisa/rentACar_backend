package src.controller.auth.authentication.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Security patch V01: credentials moved from query parameters to request body.
 * POST /api/v1/auth/isUserTrue accepts this DTO so the password never appears
 * in URL paths, server access logs, browser history, or Referer headers.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IsUserTrueRequest {
    @Email
    @NotBlank(message = "Email address cannot be blank")
    private String email;
    @NotBlank(message = "Password cannot be blank")
    private String password;
}
