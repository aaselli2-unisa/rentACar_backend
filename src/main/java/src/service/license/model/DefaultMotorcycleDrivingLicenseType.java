package src.service.license.model;

public enum DefaultMotorcycleDrivingLicenseType {

    //-----------Motorcycles----------
    NONE("None"),   //None
    M("Motorized bicycle"),   //Motorized bicycle
    A1("Motorcycle up to 125 cc"),  //Motorcycle up to 125 cc
    A2("Motorcycle not exceeding 35 kw"),  //Motorcycle not exceeding 35 kw
    A("Motorcycle exceeding 35 kw"),   //Motorcycle exceeding 35 kw
    B1("4-wheeled motorcycle");  //4-wheeled motorcycle

    private final String label;

    DefaultMotorcycleDrivingLicenseType(String label) {
        this.label = label;
    }

    public static DefaultMotorcycleDrivingLicenseType[] getAll() {
        return values();
    }

    public String getLabel() {
        return label;
    }

}
