package src.controller.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Security patch V01: password moved from query parameter to request body.
 * PUT /api/v1/users/updatePassword accepts this DTO so the password never
 * appears in URL paths, server access logs, or browser history.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordRequest {
    @NotNull
    @Positive
    private Integer id;
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
