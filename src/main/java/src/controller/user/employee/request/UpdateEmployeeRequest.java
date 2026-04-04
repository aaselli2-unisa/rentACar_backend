package src.controller.user.employee.request;

import jakarta.validation.constraints.*;
import lombok.*;
import src.service.user.model.DefaultUserStatus;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateEmployeeRequest {

    @NotNull(message = "id cannot be null")
    int id;

    @NotBlank(message = "Employee name cannot be blank")
    @Size(min = 2, max = 20)
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name/surname must consist of letters only.")
    String name;

    @NotBlank(message = "Employee surname cannot be blank")
    @Size(min = 2, max = 20)
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name/surname must consist of letters only.")
    String surname;
    @Email//-> Validates email format (e.g. @gmail, @hotmail).
    @NotBlank(message = "Employee email address cannot be blank")
    String emailAddress;

    @Size(min = 8, max = 30)
    @NotBlank(message = "Employee password cannot be blank")
    String password;
    @NotBlank(message = "Employee phone number cannot be blank")
    @Size(min = 10, max = 10, message = "Phone number must be 10 digits.")
    @Pattern(regexp = "^[0-9]+$", message = "Phone number must consist of digits only.")
    String phoneNumber;
    @NotNull(message = "Salary cannot be null")
    @Min(0)
    Double salary;
    private DefaultUserStatus status;
    private int userImageEntityId;


}
