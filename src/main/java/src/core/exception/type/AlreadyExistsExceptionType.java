package src.core.exception.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlreadyExistsExceptionType {

    USER_ALREADY_EXISTS(2001, "User already exists"),

    CUSTOMER_ALREADY_EXISTS(2002, "Customer already exists"),

    ADMIN_ALREADY_EXISTS(2003, "Admin already exists"),

    EMPLOYEE_ALREADY_EXISTS(2004, "Employee already exists"),

    //------------------------------------------------------------------

    //ALREADY EXISTS Types for Items:
    BRAND_ALREADY_EXISTS(2005, "Brand already exists"),

    COLOR_ALREADY_EXISTS(2006, "Color already exists"),

    BODY_TYPE_ALREADY_EXISTS(2007, "Body type already exists"),

    MODEL_ALREADY_EXISTS(2008, "Model already exists"),

    CAR_ALREADY_EXISTS(2009, "Vehicle already exists"),

    RENTAL_ALREADY_EXISTS(2010, "Rental already exists"),

    PAYMENT_DETAILS_ALREADY_EXISTS(2011, "Payment details already exist"),

    DISCOUNT_ALREADY_EXISTS(2012, "Discount already exists"),

    LICENSE_PLATE_ALREADY_EXISTS(2013, "License plate already exists"),
    PHONE_NUMBER_ALREADY_EXISTS(2014, "This phone number already exists"),
    DRIVING_LICENSE_NUMBER_ALREADY_EXISTS(2015, "Please enter a valid driving license number"),
    DRIVING_LICENSE_TYPE_ALREADY_EXISTS(2016, "Driving license type already exists"),
    SHIFT_TYPE_ALREADY_EXISTS(2017, "Shift type already exists"),
    VEHICLE_STATUS_ALREADY_EXISTS(2018, "Vehicle status already exists"),

    CAR_SEGMENT_ALREADY_EXSISTS(2019, "Vehicle segment already exists"),

    PAYMENT_TYPE_ALREADY_EXISTS(2020, "Payment type already exists"),
    EMAIL_ADDRESS_ALREADY_EXISTS(2021, "This email already exists");

    //------------------------------------------------------------------
    private final Integer errorCode;
    private final String message;
}
