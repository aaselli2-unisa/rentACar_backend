package src.controller.vehicle.features.common.fuel.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateFuelTypeRequest {
    @NotNull(message = "ID cannot be null")
    int id;

    @NotBlank(message = "Fuel type name cannot be blank")
    @Size(min = 2, message = "Fuel type name must be at least 2 characters.")
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "Fuel type name must consist of letters and spaces only.")
    String name;

}
