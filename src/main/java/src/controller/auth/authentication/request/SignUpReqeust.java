package src.controller.auth.authentication.request;

import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    // V-08: enforce complexity — uppercase, lowercase, digit, special char required
    @NotBlank(message = "Password cannot be blank")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,30}$",
            message = "Password must be 8-30 characters and contain at least one uppercase letter, one lowercase letter, one digit, and one special character (@$!%*?&)"
    )
    String password;

    // V-12: disallow all-zeros and other trivially invalid numbers
    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^[1-9][0-9]{9}$", message = "Phone number must be 10 digits and must not start with 0.")
    String phoneNumber;

    Double salary;

    String drivingLicenseNumber;

    int drivingLicenseTypeEntityId;

    @NotNull
    UserRole authority;

    /**
     * Security patch V02 — only CUSTOMER role is allowed through the public signup endpoint.
     * ADMIN and EMPLOYEE accounts must be created via the protected admin-only endpoints.
     */
    @AssertTrue(message = "Only CUSTOMER role is allowed for public signup")
    @JsonIgnore
    public boolean isAuthorityCustomer() {
        return authority == UserRole.CUSTOMER;
    }

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
