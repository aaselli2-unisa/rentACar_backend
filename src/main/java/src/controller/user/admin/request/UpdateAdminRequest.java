package src.controller.user.admin.request;

import jakarta.validation.constraints.*;
import lombok.*;
import src.service.user.model.DefaultUserStatus;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateAdminRequest {

    @NotNull(message = "Salary cannot be null")
    @Min(0)
    Double salary;
    @NotNull(message = "ID cannot be null")
    private int id;
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 20)
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name/surname must consist of letters only.")
    private String name;
    @NotBlank(message = "Surname cannot be blank")
    @Size(min = 2, max = 20)
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name/surname must consist of letters only.")
    private String surname;
    @NotBlank(message = "Admin email address cannot be blank")
    @Email//-> Validates email format (e.g. @gmail, @hotmail).
    private String emailAddress;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 30)
    private String password;

    @NotBlank(message = "Employee phone number cannot be blank")
    @Size(min = 10, max = 10, message = "Phone number must be 10 digits.")
    @Pattern(regexp = "^[0-9]+$", message = "Phone number must consist of digits only.")
    private String phoneNumber;
    private DefaultUserStatus status;
    private int userImageEntityId;


}
