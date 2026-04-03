package src.service.vehicle.features.common.shift.model;

public enum DefaultShiftType {

    SEMI_AUTO("Semi-Automatic"),
    MANUAL("Manual"),
    AUTOMATIC("Automatic"),
    TRIPTONIC("Triptonic"),
    NO_GEAR("No Gear");
    private final String label;

    DefaultShiftType(String label) {
        this.label = label;
    }

    public static DefaultShiftType[] getAll() {
        return values();
    }

    public String getLabel() {
        return label;
    }
}
