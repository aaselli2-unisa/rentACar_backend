package src.controller.user.customer.request;

import jakarta.validation.constraints.*;
import lombok.*;
import src.service.user.model.DefaultUserStatus;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateCustomerRequest {

    @NotNull(message = "Customer ID cannot be null")
    int id;
    @NotNull
    int drivingLicenseTypeEntityId;
    @NotBlank(message = "Customer name cannot be blank")
    @Size(min = 2, max = 20)
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name/surname must consist of letters only.")
    private String name;
    @NotBlank(message = "Customer surname cannot be blank")
    @Size(min = 2, max = 20)
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name/surname must consist of letters only.")
    private String surname;

    @NotBlank(message = "Customer email address cannot be blank")
    @Email//-> Validates email format (e.g. @gmail, @hotmail).
    private String emailAddress;

    @Size(min = 8, max = 30)
    private String password;

    @NotBlank(message = "Employee phone number cannot be blank")
    @Size(min = 10, max = 10, message = "Phone number must be 10 digits.")
    @Pattern(regexp = "^[0-9]+$", message = "Phone number must consist of digits only.")
    private String phoneNumber;
    @NotBlank(message = "Driving license number cannot be blank")
    @Size(max = 6, message = "Driving license number must be 6 digits.")
    private String drivingLicenseNumber;
    private DefaultUserStatus status;
    private int userImageEntityId;

}
