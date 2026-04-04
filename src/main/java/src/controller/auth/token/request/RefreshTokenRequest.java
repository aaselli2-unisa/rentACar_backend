package src.controller.auth.token.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenRequest {
    @Email//-> Validates email format (e.g. @gmail, @hotmail).
    @NotBlank(message = "Email address cannot be blank")
    private String email;
    @NotBlank(message = "Token cannot be blank")
    private String token;

}
