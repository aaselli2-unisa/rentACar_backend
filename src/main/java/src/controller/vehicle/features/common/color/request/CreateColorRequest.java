package src.controller.vehicle.features.common.color.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateColorRequest {

    @NotBlank(message = "Color name cannot be blank")
    @Size(min = 2, message = "Color name must be at least 2 characters.")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Color name must consist of letters only.")
    String colorEntityName;
}
