package src.core.exception.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotFoundExceptionType {

    GENERIC_EXCEPTION(1, "Unknown error"),

    //DATA NOT FOUND Types for Users:
    USER_DATA_NOT_FOUND(1001, "User not found"),
    USER_LIST_NOT_FOUND(1002, "No user found matching the given criteria"),
    CUSTOMER_DATA_NOT_FOUND(1003, "Customer not found"),
    CUSTOMER_LIST_NOT_FOUND(1004, "No customer found matching the given criteria"),
    ADMIN_DATA_NOT_FOUND(1005, "Admin not found"),
    ADMIN_LIST_NOT_FOUND(1006, "No admin found matching the given criteria"),
    EMPLOYEE_DATA_NOT_FOUND(1007, "Employee not found"),
    EMPLOYEE_LIST_NOT_FOUND(1008, "No employee found matching the given criteria"),

    //------------------------------------------------------------------

    //DATA NOT FOUND Types for Items:
    BRAND_DATA_NOT_FOUND(1009, "Brand not found"),
    BRAND_LIST_NOT_FOUND(1010, "No brand found matching the given criteria"),

    COLOR_DATA_NOT_FOUND(1011, "Color not found"),
    COLOR_LIST_NOT_FOUND(1012, "No color found matching the given criteria"),
    BODY_TYPE_DATA_NOT_FOUND(1013, "Body type not found"),
    BODY_TYPE_LIST_NOT_FOUND(1014, "No body type found matching the given criteria"),

    MODEL_DATA_NOT_FOUND(1015, "Model not found"),
    MODEL_LIST_NOT_FOUND(1016, "No model found matching the given criteria"),

    CAR_DATA_NOT_FOUND(1017, "Vehicle not found"),
    CAR_LIST_NOT_FOUND(1018, "No vehicle found matching the given criteria"),

    RENTAL_DATA_NOT_FOUND(1019, "Rental record not found"),
    RENTAL_LIST_NOT_FOUND(1020, "No rental record found matching the given criteria"),

    PAYMENT_DETAILS_DATA_NOT_FOUND(1021, "Payment details not found"),
    PAYMENT_DETAILS_LIST_NOT_FOUND(1022, "No payment details found matching the given criteria"),

    PAYMENT_TYPE_NOT_FOUND(1023, "Payment method not found"),
    PAYMENT_TYPE_LIST_NOT_FOUND(1024, "No payment method found matching the given criteria"),
    DISCOUNT_CODE_NOT_FOUND(1025, "Discount code not found"),
    DISCOUNT_CODE_LIST_NOT_FOUND(1026, "No discount code found matching the given criteria"),

    FUEL_TYPE_NOT_FOUND(1027, "Fuel type not found"),
    FUEL_TYPE_LIST_NOT_FOUND(1028, "No fuel type found matching the given criteria"),
    SHIFT_TYPE_NOT_FOUND(1029, "Shift type not found"),
    SHIFT_TYPE_LIST_NOT_FOUND(1030, "No shift type found matching the given criteria"),

    VEHICLE_STATUS_NOT_FOUND(1031, "Vehicle status not found"),

    VEHICLE_STATUS_LIST_NOT_FOUND(1032, "No vehicle status found matching the given criteria"),

    DRIVING_LICENSE_TYPE_NOT_FOUND(1033, "Driving license type not found"),
    DRIVING_LICENSE_TYPE_LIST_NOT_FOUND(1034, "No driving license type found matching the given criteria"),

    CAR_SEGMENT_NOT_FOUND(1035, "Vehicle segment not found"),
    CAR_SEGMENT_LIST_NOT_FOUND(1036, "No vehicle segment found matching the given criteria"),

    IMAGE_NOT_FOUND(1037, "Photo not found"),
    IMAGE_LIST_NOT_FOUND(1038, "No photo found matching the given criteria"),

    USER_ROLE_NOT_FOUND(1039, "User role not found"),

    RENTAL_STATUS_NOT_FOUND(1040, "Rental status not found"),
    RENTAL_STATUS_LIST_NOT_FOUND(1041, "No rental status found matching the given criteria"),
    EMAIL_ADDRESS_NOT_FOUND(1042, "No user found for this email address"),
    OTP_NOT_FOUND(1043, "Verification token not found or already used");
    //------------------------------------------------------------------
    private final Integer errorCode;
    private final String message;
}
