package src.controller.vehicle.features.common.status.request;

import jakarta.validation.constraints.*;
import lombok.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateVehicleStatusRequest {
    @NotNull
    @Min(1)
    int id;

    @NotBlank(message = "Vehicle status name cannot be blank")
    @Size(min = 2, message = "Vehicle status name must be at least 2 characters.")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Vehicle status name must consist of letters and spaces only.")
    String name;

}
