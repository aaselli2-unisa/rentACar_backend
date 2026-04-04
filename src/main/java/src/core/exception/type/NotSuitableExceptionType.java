package src.core.exception.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotSuitableExceptionType {

    DRIVING_LICENSE_TYPE_NOT_SUITABLE(5000, "Driving license type is not suitable"),
    RENTAL_IS_NOT_ACTIVE(5001, "Rental record is not active");
    //------------------------------------------------------------------
    private final Integer errorCode;
    private final String message;
}
