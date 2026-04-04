package src.controller.vehicle.features.car.body.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCarBodyTypeRequest {

    @NotBlank(message = "Body type name cannot be blank")
    @Size(min = 2, message = "Body type name must be at least 2 characters.")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Body type name must consist of letters only.")
    String carBodyTypeEntityName;


}
