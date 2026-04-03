package src.service.vehicle.features.common.color.model;

public enum DefaultColors {

    WHITE("White"),

    BLACK("Black"),

    RED("Red"),

    GREY("Grey"),

    BLUE("Blue");
    private final String label;

    DefaultColors(String label) {
        this.label = label;
    }

    public static DefaultColors[] getAll() {
        return values();
    }

    public String getLabel() {
        return label;
    }


}
