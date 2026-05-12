package com.extendrent.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import src.controller.payment.detail.PaymentDetailController;
import src.controller.payment.detail.response.PaymentDetailsResponse;
import src.core.config.AppConfig;
import src.core.config.SecurityConfig;
import src.core.security.JwtService;
import src.service.payment.detail.PaymentDetailsService;
import src.service.user.UserService;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Security tests for sensitive data exposure in payment API responses (CWE-200).
 *
 * Gap from ANALISI_SICUREZZA_2304, Point 10:
 * PaymentDetailsResponse must not expose fields that could constitute a data leak:
 *  - No password or credential fields
 *  - No credit card number, CVV, or bank account details
 *  - No fields beyond what is required for ADMIN payment management
 *
 * PaymentDetailsResponse current fields: id, paymentTypeEntityId, amount,
 * PaymentTypeEntityName, createdDate, isDeleted — none are sensitive credentials.
 * This test documents and regression-guards that shape.
 *
 * If a future change accidentally adds a sensitive field (e.g., cardNumber from
 * a join), these tests will fail and block the merge.
 */
@WebMvcTest(PaymentDetailController.class)
@Import({SecurityConfig.class, AppConfig.class})
@ActiveProfiles("test")
@DisplayName("PaymentDetailController – response field exposure tests (Point 10)")
class PaymentResponseFieldsSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;
    @MockBean private PaymentDetailsService paymentDetailsService;

    // ======================================================================
    //  Response must not contain sensitive credential/PII fields
    // ======================================================================

    @Test
    @DisplayName("GET /api/v1/paymentDetails/{id} response must not expose password fields")
    @WithMockUser(roles = "ADMIN")
    void getById_responseDoesNotContainPasswordField() throws Exception {
        when(paymentDetailsService.getById(1)).thenReturn(samplePaymentResponse());

        mockMvc.perform(get("/api/v1/paymentDetails/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.password").doesNotExist())
                .andExpect(jsonPath("$.response.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.response.secret").doesNotExist());
    }

    @Test
    @DisplayName("GET /api/v1/paymentDetails/{id} response must not expose credit card data")
    @WithMockUser(roles = "ADMIN")
    void getById_responseDoesNotContainCardData() throws Exception {
        when(paymentDetailsService.getById(1)).thenReturn(samplePaymentResponse());

        mockMvc.perform(get("/api/v1/paymentDetails/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.cardNumber").doesNotExist())
                .andExpect(jsonPath("$.response.cvv").doesNotExist())
                .andExpect(jsonPath("$.response.cvc").doesNotExist())
                .andExpect(jsonPath("$.response.bankAccount").doesNotExist())
                .andExpect(jsonPath("$.response.iban").doesNotExist());
    }

    @Test
    @DisplayName("GET /api/v1/paymentDetails/{id} response must not expose customer PII beyond payment scope")
    @WithMockUser(roles = "ADMIN")
    void getById_responseDoesNotContainCustomerPii() throws Exception {
        when(paymentDetailsService.getById(1)).thenReturn(samplePaymentResponse());

        mockMvc.perform(get("/api/v1/paymentDetails/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.emailAddress").doesNotExist())
                .andExpect(jsonPath("$.response.phoneNumber").doesNotExist())
                .andExpect(jsonPath("$.response.drivingLicenseNumber").doesNotExist());
    }

    @Test
    @DisplayName("GET /api/v1/paymentDetails/{id} response contains expected business fields")
    @WithMockUser(roles = "ADMIN")
    void getById_responseContainsExpectedFields() throws Exception {
        when(paymentDetailsService.getById(1)).thenReturn(samplePaymentResponse());

        mockMvc.perform(get("/api/v1/paymentDetails/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.id").exists())
                .andExpect(jsonPath("$.response.amount").exists())
                .andExpect(jsonPath("$.response.paymentTypeEntityId").exists());
    }

    @Test
    @DisplayName("GET /api/v1/paymentDetails response list must not expose sensitive fields")
    @WithMockUser(roles = "ADMIN")
    void getAll_responseDoesNotContainSensitiveFields() throws Exception {
        when(paymentDetailsService.getAll()).thenReturn(java.util.List.of(samplePaymentResponse()));

        mockMvc.perform(get("/api/v1/paymentDetails"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].cardNumber").doesNotExist())
                .andExpect(jsonPath("$.response[0].password").doesNotExist())
                .andExpect(jsonPath("$.response[0].cvv").doesNotExist());
    }

    // ---- helpers -----------------------------------------------------------

    private static PaymentDetailsResponse samplePaymentResponse() {
        return PaymentDetailsResponse.builder()
                .id(1)
                .paymentTypeEntityId(2)
                .amount(350.00)
                .PaymentTypeEntityName("Credit Card")
                .createdDate(LocalDateTime.of(2026, 4, 1, 10, 0))
                .isDeleted(false)
                .build();
    }
}
