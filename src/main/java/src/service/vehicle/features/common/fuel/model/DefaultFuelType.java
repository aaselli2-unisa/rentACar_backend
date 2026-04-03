package src.service.vehicle.features.common.fuel.model;

public enum DefaultFuelType {

    PETROL("Petrol"),
    DIESEL("Diesel"),
    ELECTRIC("Electric"),
    HYBRID("Hybrid"),
    LPG("LPG"),
    GASOLINE_LPG("Gasoline LPG"),
    NO_FUEL("No Fuel");
    private final String label;

    DefaultFuelType(String label) {
        this.label = label;
    }

    public static DefaultFuelType[] getAll() {
        return values();
    }

    public String getLabel() {
        return label;
    }
}
