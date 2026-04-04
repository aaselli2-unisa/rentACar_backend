package src.service.license.model;

public enum DefaultCarDrivingLicenseType {


    //-----------Cars----------
    NONE("None"),   //None
    B("Passenger car and light van"),   //Passenger car and light van
    BE("Passenger car and light van with trailer"),  //Passenger car and light van with trailer
    C1("Truck and tractor up to 7500 kg"),  //Truck and tractor up to 7500 kg
    C1E("Truck and tractor up to 12000 kg"), //Truck and tractor up to 12000 kg
    C("Truck and tractor"),   //Truck and tractor
    CE("Truck and tractor with trailer"),  //Truck and tractor with trailer
    D1("Minibus"),  //Minibus
    D1E("Minibus with trailer"), //Minibus with trailer
    D("Minibus and bus"),   //Minibus and bus
    E("Minibus and bus with trailer"),   //Minibus and bus with trailer
    F("Rubber-wheeled tractor"),   //Rubber-wheeled tractor
    G("Construction machinery");    //Construction machinery
    private final String label;

    DefaultCarDrivingLicenseType(String label) {
        this.label = label;
    }

    public static DefaultCarDrivingLicenseType[] getAll() {
        return values();
    }

    public String getLabel() {
        return label;
    }

}
