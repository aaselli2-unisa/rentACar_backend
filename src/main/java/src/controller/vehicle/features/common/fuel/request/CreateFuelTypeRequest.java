package src.controller.vehicle.features.common.fuel.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateFuelTypeRequest {

    @NotBlank(message = "Fuel type name cannot be blank")
    @Size(min = 2, message = "Fuel type name must be at least 2 characters.")
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "Fuel type name must consist of letters and spaces only.")
    String name;

}
