package src.service.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import src.controller.payment.CreditCardInformation;
import src.core.exception.DataNotFoundException;
import src.core.exception.PaymentException;
import src.repository.payment.detail.PaymentDetailsEntityServiceImpl;
import src.service.businessrules.abstracts.BaseRules;

import java.time.LocalDate;
import java.util.List;

import static src.core.exception.type.NotFoundExceptionType.PAYMENT_DETAILS_LIST_NOT_FOUND;
import static src.core.exception.type.PaymentExceptionType.EXPIRY_DATE_HAS_EXPIRED;
import static src.core.exception.type.PaymentExceptionType.INVALID_CARD_NUMBER;

@RequiredArgsConstructor
@Service
public class PaymentRules implements BaseRules {

    private final PaymentDetailsEntityServiceImpl paymentDetailsEntityServiceImpl;

    //--------------------- AUTO FIX METHODS ---------------------
    public CreditCardInformation fixCreditCardInformation(CreditCardInformation creditCardInformation) {
        creditCardInformation.setCardOwnerName(this.fixCreditCardOwnerName(creditCardInformation.getCardOwnerName()));
        creditCardInformation.setCardOwnerSurname(this.fixCreditCardOwnerSurname(creditCardInformation.getCardOwnerSurname()));
        return creditCardInformation;
    }

    //---------------AUTO CHECKING METHODS--------------------------------
    public void checkCreditCard(CreditCardInformation creditCardInformation) {
        this.checkCreditCardNumber(creditCardInformation.getCardNumber());
        this.checkOwnerOfCreditCardFullName(creditCardInformation.getCardOwnerName(), creditCardInformation.getCardOwnerSurname());
        this.checkCreditCardExpirationDate(creditCardInformation.getExpirationDate()); // V-11: wire up expiry check
    }


    //----------------------------METHODS--------------------------------

    public String fixCreditCardOwnerName(String name) {
        return name.replace(" ", "").toUpperCase();
    }

    public String fixCreditCardOwnerSurname(String surname) {
        return surname.replace(" ", "").toUpperCase();
    }


    // V-10: Luhn algorithm — rejects structurally invalid card numbers
    private void checkCreditCardNumber(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = cardNumber.charAt(i) - '0';
            if (alternate) {
                n *= 2;
                if (n > 9) n -= 9;
            }
            sum += n;
            alternate = !alternate;
        }
        if (sum % 10 != 0) {
            throw new PaymentException(INVALID_CARD_NUMBER);
        }
    }

    private void checkOwnerOfCreditCardFullName(String name, String surname) {
        if (name == null || name.isBlank() || surname == null || surname.isBlank()) {
            throw new PaymentException(INVALID_CARD_NUMBER);
        }
    }

    // V-11: logic was inverted (threw when card was valid); now throws when expired
    // Compare by month boundary — expiry day is irrelevant for card validity
    private void checkCreditCardExpirationDate(LocalDate expirationDate) {
        if (expirationDate.isBefore(LocalDate.now().withDayOfMonth(1))) {
            throw new PaymentException(EXPIRY_DATE_HAS_EXPIRED);
        }
    }


    @Override
    public void checkDataList(List<?> list) {
        if (list.isEmpty()) {
            throw new DataNotFoundException(PAYMENT_DETAILS_LIST_NOT_FOUND);
        }

    }

    @Override
    public String fixName(String name) {
        return name;
    }
}
