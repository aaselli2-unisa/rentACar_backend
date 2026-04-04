package src.controller.user.customer.request;

import jakarta.validation.constraints.*;
import lombok.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CreateCustomerRequest {

    @NotBlank(message = "Customer name cannot be blank")
    @Size(min = 2, max = 20)
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name/surname must consist of letters only.")
    String name;

    @NotBlank(message = "Customer surname cannot be blank")
    @Size(min = 2, max = 20)
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name/surname must consist of letters only.")
    String surname;
    @NotBlank(message = "Customer email address cannot be blank")
    @Email//-> Validates email format (e.g. @gmail, @hotmail).
    String emailAddress;
    @NotBlank(message = "Customer password cannot be blank")
    @Size(min = 8, max = 30)
    String password;

    @NotBlank(message = "Employee phone number cannot be blank")
    @Size(min = 10, max = 10, message = "Phone number must be 10 digits.")
    @Pattern(regexp = "^[0-9]+$", message = "Phone number must consist of digits only.")
    String phoneNumber;
    @Size(max = 6, message = "Driving license number must be 6 digits.")
    String drivingLicenseNumber;
    @NotNull
    int drivingLicenseTypeEntityId;
    @NotNull
    private int userImageEntityId;


}
