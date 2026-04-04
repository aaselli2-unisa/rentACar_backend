package src.controller.vehicle.features.common.status.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import src.service.vehicle.features.common.status.model.DefaultVehicleStatus;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateVehicleStatusRequest {

    @NotBlank(message = "Vehicle status name cannot be blank")
    @Size(min = 2, message = "Vehicle status name must be at least 2 characters.")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Vehicle status name must consist of letters and spaces only.")
    String name;

    @Pattern(regexp = "^[A-Z-_]+$", message = "Vehicle status type must consist of uppercase letters only, with no spaces.")
    DefaultVehicleStatus vehicleStatus;

}
