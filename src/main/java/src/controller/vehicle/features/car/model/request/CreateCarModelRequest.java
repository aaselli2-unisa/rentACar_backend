package src.controller.vehicle.features.car.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCarModelRequest {
    @NotNull(message = "Brand ID cannot be null")
    int brandEntityId;

    @NotBlank(message = "Model name cannot be blank")
    @Size(min = 2, message = "Model name must be at least 2 characters.")
    String carModelEntityName;


}
