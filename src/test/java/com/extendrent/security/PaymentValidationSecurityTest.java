package com.extendrent.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import src.controller.payment.CreditCardInformation;
import src.core.exception.PaymentException;
import src.core.exception.type.PaymentExceptionType;
import src.service.payment.PaymentRules;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;

/**
 * V-10 – Payment card validation: Luhn check and expiry enforcement (CWE-20 / PCI-DSS Req 6).
 *
 * Previously:
 *   - checkCreditCardNumber() was an empty stub — any card number, including structurally
 *     impossible ones (0000000000000000), was accepted.
 *   - checkCreditCardExpirationDate() had inverted logic: it threw when the card was VALID
 *     (date in the future) and passed silently when the card was EXPIRED (date in the past).
 *   - checkCreditCardExpirationDate() was never called from checkCreditCard(), so expired
 *     cards were always accepted.
 *
 * Fix (V-10): Luhn algorithm implemented in checkCreditCardNumber().
 * Fix (V-11): Expiry logic corrected; checkCreditCardExpirationDate() wired into checkCreditCard().
 */
@DisplayName("V-10/V-11 – Credit card Luhn validation and expiry enforcement")
class PaymentValidationSecurityTest {

    private final PaymentRules rules = new PaymentRules(mock(
            src.repository.payment.detail.PaymentDetailsEntityServiceImpl.class));

    // -----------------------------------------------------------------------
    //  V-10: Luhn algorithm
    // -----------------------------------------------------------------------

    @ParameterizedTest(name = "Luhn-valid card number {0} is accepted")
    @ValueSource(strings = {
            "4532015112830366",  // Visa test number
            "5425233430109903",  // Mastercard test number
            "4111111111111111",  // Commonly used Visa test number
            "4916338506082832",  // Additional Visa test number
    })
    @DisplayName("V-10: Luhn-valid card numbers do not throw PaymentException")
    void luhnValid_cardNumbers_areAccepted(String cardNumber) throws Exception {
        java.lang.reflect.Method method = PaymentRules.class
                .getDeclaredMethod("checkCreditCardNumber", String.class);
        method.setAccessible(true);
        // Should not throw
        assertThatCode(() -> method.invoke(rules, cardNumber))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest(name = "Luhn-invalid card number {0} is rejected")
    @ValueSource(strings = {
            "9999999999999999",  // All nines — sum=144, 144%10≠0
            "1234567890123456",  // Random digits — sum=64, 64%10≠0
            "4532015112830367",  // Off-by-one from a valid Visa
            "1111111111111111",  // All ones — sum=24, 24%10≠0
            // Note: 0000000000000000 is NOT included — all-zeros passes Luhn (sum=0, 0%10=0)
    })
    @DisplayName("V-10: Luhn-invalid card numbers throw INVALID_CARD_NUMBER exception")
    void luhnInvalid_cardNumbers_areRejected(String cardNumber) throws Exception {
        java.lang.reflect.Method method = PaymentRules.class
                .getDeclaredMethod("checkCreditCardNumber", String.class);
        method.setAccessible(true);
        assertThatThrownBy(() -> {
            try {
                method.invoke(rules, cardNumber);
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw e.getCause();
            }
        })
        .isInstanceOf(PaymentException.class)
        .extracting(e -> ((PaymentException) e).getPaymentExceptionType())
        .isEqualTo(PaymentExceptionType.INVALID_CARD_NUMBER);
    }

    // -----------------------------------------------------------------------
    //  V-11: Expiry date logic
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("V-11: Card with future expiry date is accepted (no exception)")
    void futureExpiryDate_isAccepted() throws Exception {
        java.lang.reflect.Method method = PaymentRules.class
                .getDeclaredMethod("checkCreditCardExpirationDate", LocalDate.class);
        method.setAccessible(true);
        LocalDate futureDate = LocalDate.now().plusYears(2);
        assertThatCode(() -> method.invoke(rules, futureDate))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("V-11: Card with past expiry date throws EXPIRY_DATE_HAS_EXPIRED (was: wrong direction)")
    void pastExpiryDate_throwsExpiredException() throws Exception {
        java.lang.reflect.Method method = PaymentRules.class
                .getDeclaredMethod("checkCreditCardExpirationDate", LocalDate.class);
        method.setAccessible(true);
        LocalDate expiredDate = LocalDate.now().minusYears(1);
        assertThatThrownBy(() -> {
            try {
                method.invoke(rules, expiredDate);
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw e.getCause();
            }
        })
        .isInstanceOf(PaymentException.class)
        .extracting(e -> ((PaymentException) e).getPaymentExceptionType())
        .isEqualTo(PaymentExceptionType.EXPIRY_DATE_HAS_EXPIRED);
    }

    @Test
    @DisplayName("V-11: checkCreditCard() calls expiry validation (expired card → exception)")
    void checkCreditCard_wiresUpExpiryCheck() {
        // Before fix, checkCreditCard() never called checkCreditCardExpirationDate().
        // This test verifies the wire-up is in place.
        CreditCardInformation expiredCard = CreditCardInformation.builder()
                .cardNumber("4111111111111111") // Luhn-valid
                .cardOwnerName("ALICE")
                .cardOwnerSurname("SMITH")
                .expirationDate(LocalDate.now().minusYears(1)) // expired
                .cvc("123")
                .build();

        assertThatThrownBy(() -> rules.checkCreditCard(expiredCard))
                .isInstanceOf(PaymentException.class)
                .extracting(e -> ((PaymentException) e).getPaymentExceptionType())
                .isEqualTo(PaymentExceptionType.EXPIRY_DATE_HAS_EXPIRED);
    }

    @Test
    @DisplayName("V-11: checkCreditCard() passes for a valid card (Luhn-valid + future expiry)")
    void checkCreditCard_validCard_noException() {
        CreditCardInformation validCard = CreditCardInformation.builder()
                .cardNumber("4111111111111111") // Luhn-valid Visa
                .cardOwnerName("ALICE")
                .cardOwnerSurname("SMITH")
                .expirationDate(LocalDate.now().plusYears(2))
                .cvc("123")
                .build();

        assertThatCode(() -> rules.checkCreditCard(validCard))
                .doesNotThrowAnyException();
    }
}
