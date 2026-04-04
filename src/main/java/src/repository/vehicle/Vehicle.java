package src.repository.vehicle;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import src.core.BaseEntity;
import src.repository.license.DrivingLicenseTypeEntity;
import src.repository.vehicle.features.common.color.ColorEntity;
import src.repository.vehicle.features.common.fuel.FuelTypeEntity;
import src.repository.vehicle.features.common.shift.ShiftTypeEntity;
import src.repository.vehicle.features.common.status.VehicleStatusEntity;
import src.service.vehicle.model.VehicleType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass // Used to add these columns to the database table of subclasses.
@SuperBuilder(builderMethodName = "vehicleBuilder")
public class Vehicle extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "color_id")
    private ColorEntity colorEntity;

    @Column(name = "year")
    private int year;

    @Column(name = "details")
    private String details;

    @Column(name = "seat")
    private int seat;

    @Column(name = "luggage")
    private int luggage;

    @ManyToOne
    @JoinColumn(name = "fuel_type_id")
    private FuelTypeEntity fuelTypeEntity;

    @ManyToOne
    @JoinColumn(name = "shift_type_id")
    private ShiftTypeEntity shiftTypeEntity;

    @Column(name = "rental_price")
    private double rentalPrice;

    @ManyToOne
    @JoinColumn(name = "vehicle_status_id")
    private VehicleStatusEntity vehicleStatusEntity;

    @Column(name = "is_available")
    private boolean isAvailable;

    @ManyToOne
    @JoinColumn(name = "expected_min_driving_license_type_id")
    private DrivingLicenseTypeEntity expectedMinDrivingLicenseType; //-> The minimum driving license class expected from the user.

    @Column(name = "vehicle_type")
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;
}
