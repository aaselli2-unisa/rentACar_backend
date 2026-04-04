package src.controller.vehicle.car.request;

import jakarta.validation.constraints.*;
import lombok.*;
import src.service.vehicle.model.VehicleType;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CreateCarRequest {
    private final VehicleType vehicleType = VehicleType.CAR;
    @NotNull(message = "Brand ID cannot be null")
    @Min(1)
    int carImageEntityId;

    @NotNull(message = "Brand ID cannot be null")
    @Min(1)
    int brandEntityId;

    @NotNull(message = "ID cannot be null")
    @Min(1)
    int carModelEntityId;

    @NotNull(message = "Car body type ID cannot be null")
    @Min(1)
    int carBodyTypeEntityId;

    @NotNull(message = "Color ID cannot be null")
    @Min(1)
    int colorEntityId;

    @NotNull(message = "Segment ID cannot be null")
    @Min(1)
    int carSegmentEntityId;

    @NotNull(message = "Year cannot be null")
    @Min(value = 2005, message = "Year must be at least 2005.")
    @Max(value = 2024, message = "Year must be at most 2024.")
    int year;

    @Size(max = 500, message = "Description must be at most 500 characters.")
    String details;

    @DecimalMin(value = "100.0", message = "Rental price cannot be less than 110.")
    @NotNull(message = "Rental price cannot be null")
    double rentalPrice;

    @NotBlank(message = "License plate cannot be blank")
    @Pattern(regexp = "^(\\d{2}[ ]?[A-Za-z]{1,3}[ ]?\\d{2}|\\d{2}[ ]?[A-Za-z]{2}[ ]?\\d{3})$", message = "Invalid license plate format")
    String licensePlate;


    @NotNull(message = "Kilometer cannot be null")
    @Min(value = 1, message = "Kilometer must be at least 1.")
    int kilometer;

    @NotNull(message = "Expected driving license type cannot be null")
    @Min(1)
    int expectedMinDrivingLicenseTypeId;

    @NotNull
    @Min(1)
    int shiftTypeEntityId;

    @NotNull
    @Min(1)
    int fuelTypeEntityId;

    @NotNull(message = "Seat count cannot be null")
    @Min(1)
    @Max(15)
    int seat;

    @NotNull(message = "Luggage capacity cannot be null")
    @Min(1)
    @Max(15)
    int luggage;

    @NotNull(message = "Vehicle status ID cannot be null")
    @Min(1)
    int vehicleStatusEntityId;

    @NotNull
    boolean isAvailable;


}
