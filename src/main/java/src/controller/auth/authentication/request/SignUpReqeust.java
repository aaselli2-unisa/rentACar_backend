package src.controller.auth.authentication.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import src.controller.user.admin.request.CreateAdminRequest;
import src.controller.user.customer.request.CreateCustomerRequest;
import src.controller.user.employee.request.CreateEmployeeRequest;
import src.service.user.model.UserRole;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class SignUpReqeust {

    @Size(min = 2, max = 20)
    String name;

    @NotBlank(message = "Surname cannot be blank")
    @Size(min = 2, max = 20)
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Name/surname must consist of letters only.")
    String surname;

    @Email//-> Validates email format (e.g. @gmail, @hotmail).
    @NotBlank(message = "Email address cannot be blank")
    String emailAddress;

    @Size(min = 8, max = 30)
    @NotBlank(message = "Password cannot be blank")
    String password;

    @NotBlank(message = "Phone number cannot be blank")
    @Size(min = 10, max = 10, message = "Phone number must be 10 digits.")
    @Pattern(regexp = "^[0-9]+$", message = "Phone number must consist of digits only.")
    String phoneNumber;

    Double salary;

    String drivingLicenseNumber;

    int drivingLicenseTypeEntityId;

    @NotNull
    UserRole authority;

    @NotNull
    int userImageEntityId;

    public CreateCustomerRequest forCustomer() {
        return CreateCustomerRequest.builder()
                .name(name)
                .surname(surname)
                .emailAddress(emailAddress)
                .password(password)
                .phoneNumber(phoneNumber)
                .drivingLicenseNumber(drivingLicenseNumber)
                .drivingLicenseTypeEntityId(drivingLicenseTypeEntityId)
                .userImageEntityId(userImageEntityId)
                .build();
    }

    public CreateAdminRequest forAdmin() {
        return CreateAdminRequest.builder()
                .name(name)
                .surname(surname)
                .emailAddress(emailAddress)
                .password(password)
                .phoneNumber(phoneNumber)
                .salary(salary)
                .userImageEntityId(userImageEntityId)
                .build();
    }

    public CreateEmployeeRequest forEmployee() {
        return CreateEmployeeRequest.builder()
                .name(name)
                .surname(surname)
                .emailAddress(emailAddress)
                .password(password)
                .phoneNumber(phoneNumber)
                .salary(salary)
                .userImageEntityId(userImageEntityId)
                .build();
    }

}
