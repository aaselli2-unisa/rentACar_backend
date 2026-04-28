package com.extendrent.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import src.controller.payment.detail.PaymentDetailController;
import src.core.config.AppConfig;
import src.core.config.SecurityConfig;
import src.core.security.JwtService;
import src.service.payment.detail.PaymentDetailsService;
import src.service.user.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security tests for payment amount validation (OWASP A03 / CWE-20).
 *
 * Business logic security gap (from ANALISI_SICUREZZA_2304, Point 8):
 * Fraudulent payment amounts (negative) could corrupt financial records.
 *
 * UpdatePaymentDetailsRequest carries a @Min(0) constraint on the amount field.
 * These tests verify that the HTTP layer enforces this constraint and returns
 * 400 Bad Request before the service layer is ever invoked.
 *
 * Note on zero amount: @Min(0) allows zero, which is also an edge case worth
 * reviewing at the business level, but is not a validation failure by constraint.
 */
@WebMvcTest(PaymentDetailController.class)
@Import({SecurityConfig.class, AppConfig.class})
@ActiveProfiles("test")
@DisplayName("Payment amount validation – security tests (Point 8)")
class PaymentAmountValidationSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;
    @MockBean private PaymentDetailsService paymentDetailsService;

    // ======================================================================
    //  Negative amounts — must be rejected with 400 (CWE-20 input validation)
    // ======================================================================

    @ParameterizedTest(name = "Amount {0} is rejected as invalid (below @Min(0))")
    @ValueSource(doubles = {-0.01, -1.0, -100.0, -999999.99})
    @DisplayName("PUT /api/v1/paymentDetails with negative amount returns 400")
    @WithMockUser(roles = "ADMIN")
    void update_negativeAmount_returns400(double negativeAmount) throws Exception {
        String body = String.format("{\"id\":1,\"amount\":%s}", negativeAmount);
        mockMvc.perform(put("/api/v1/paymentDetails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    // ======================================================================
    //  Valid amounts — must be accepted (validation boundary check)
    // ======================================================================

    @Test
    @DisplayName("PUT /api/v1/paymentDetails with zero amount passes validation (service layer handles business logic)")
    @WithMockUser(roles = "ADMIN")
    void update_zeroAmount_passesValidation() throws Exception {
        // @Min(0) allows zero — the constraint is satisfied.
        // Whether a $0 payment is acceptable is a business-layer decision, not a
        // validation constraint. This test documents the boundary of @Min(0).
        String body = "{\"id\":1,\"amount\":0.0}";
        mockMvc.perform(put("/api/v1/paymentDetails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                // 200 if service succeeds, or 5xx if service throws — either way, not 400.
                // We verify that validation does NOT reject zero.
                .andExpect(result ->
                        org.assertj.core.api.Assertions.assertThat(result.getResponse().getStatus())
                                .isNotEqualTo(400));
    }

    @Test
    @DisplayName("PUT /api/v1/paymentDetails with positive amount passes validation")
    @WithMockUser(roles = "ADMIN")
    void update_positiveAmount_passesValidation() throws Exception {
        String body = "{\"id\":1,\"amount\":250.00}";
        mockMvc.perform(put("/api/v1/paymentDetails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(result ->
                        org.assertj.core.api.Assertions.assertThat(result.getResponse().getStatus())
                                .isNotEqualTo(400));
    }

    // ======================================================================
    //  Malformed body — must be rejected with 4xx
    // ======================================================================

    @Test
    @DisplayName("PUT /api/v1/paymentDetails with non-numeric amount string returns 400")
    @WithMockUser(roles = "ADMIN")
    void update_nonNumericAmount_returns400() throws Exception {
        // Jackson cannot deserialize "abc" as double → 400 HttpMessageNotReadableException.
        String body = "{\"id\":1,\"amount\":\"not-a-number\"}";
        mockMvc.perform(put("/api/v1/paymentDetails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
