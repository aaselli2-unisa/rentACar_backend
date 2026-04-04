package src.controller.auth.authentication.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignInRequest {
    @Email//-> Validates email format (e.g. @gmail, @hotmail).
    @NotBlank(message = "Email address cannot be blank")
    private String email;
    @NotBlank(message = "Password cannot be blank")
    private String password;
}