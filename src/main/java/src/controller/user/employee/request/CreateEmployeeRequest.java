package src.controller.user.employee.request;

import jakarta.validation.constraints.*;
import lombok.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CreateEmployeeRequest {

    @NotBlank(message = "Name cannot be blank")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name/surname must consist of letters only.")
    @Size(min = 2, max = 20)
    private String name;
    @NotBlank(message = "Surname cannot be blank")
    @Size(min = 2, max = 20)
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name/surname must consist of letters only.")
    private String surname;
    @Email//-> Validates email format (e.g. @gmail, @hotmail).

    @NotBlank(message = "Admin email address cannot be blank")
    private String emailAddress;
    @Size(min = 8, max = 30)
    @NotBlank(message = "Password cannot be blank")
    private String password;
    @NotBlank(message = "Employee phone number cannot be blank")
    @Size(min = 10, max = 10, message = "Phone number must be 10 digits.")
    @Pattern(regexp = "^[0-9]+$", message = "Phone number must consist of digits only.")
    private String phoneNumber;
    @NotNull(message = "Salary cannot be null")
    @Min(0)
    private Double salary;
    private int userImageEntityId;


}
