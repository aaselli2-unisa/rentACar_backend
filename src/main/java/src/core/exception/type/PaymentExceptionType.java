package src.core.exception.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentExceptionType {


    PAYMENT_REJECTED(4000, "Payment was rejected by your bank"),
    CREDIT_CARD_INFORMATION_NOT_VERIFIED(4001, "Your credit card information could not be verified"),
    EXPIRY_DATE_HAS_EXPIRED(4002, "Your credit card has expired"),
    PAYMENT_TYPE_IS_NOT_ACTIVE(4003, "Payment type is not active"),
    NOT_SUPPORTED_PAYMENT_TYPE(4004, "Unsupported payment type"),
    PAYMENT_ERROR(4005, "Payment error"),
    INVALID_CARD_NUMBER(4006, "Invalid card number");
    //------------------------------------------------------------------
    private final Integer errorCode;
    private final String message;

}
