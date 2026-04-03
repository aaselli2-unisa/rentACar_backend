package src.service.vehicle.features.common.status.model;

public enum DefaultVehicleStatus {
    AVAILABLE("Available"),
    IN_USE("In Use"),
    MAINTENANCE("Maintenance"),
    UNAVAILABLE("Unavailable"),
    BOOKED("Booked"),
    DELETED("Deleted");

    private final String label;

    DefaultVehicleStatus(String label) {
        this.label = label;
    }

    public static DefaultVehicleStatus[] getAll() {
        return values();
    }

    public String getLabel() {
        return label;
    }
}
