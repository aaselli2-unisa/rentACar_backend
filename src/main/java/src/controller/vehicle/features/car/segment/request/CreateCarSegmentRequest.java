package src.controller.vehicle.features.car.segment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCarSegmentRequest {

    @NotBlank(message = "Segment name cannot be blank")
    @Size(min = 2, message = "Segment name must be at least 2 characters.")
    String name;


}
