package src.controller.vehicle.features.common.shift.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateShiftTypeRequest {

    @NotBlank(message = "Shift type name cannot be blank")
    @Size(min = 2, message = "Shift type name must be at least 2 characters.")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Shift type name must consist of letters and spaces only.")
    String name;
}
