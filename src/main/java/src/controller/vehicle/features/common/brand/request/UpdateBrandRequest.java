package src.controller.vehicle.features.common.brand.request;

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
public class UpdateBrandRequest {
    @NotNull(message = "ID cannot be null")
    int id;

    @NotBlank(message = "Brand name cannot be blank")
    @Size(min = 2, message = "Brand name must be at least 2 characters.")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Brand name must consist of letters only.")
    String name;

    int brandImageEntityId;


}
