package com.extendrent.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import src.controller.payment.detail.PaymentDetailController;
import src.core.config.AppConfig;
import src.core.config.SecurityConfig;
import src.core.security.JwtService;
import src.service.payment.detail.PaymentDetailsService;
import src.service.user.UserService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security tests for {@link PaymentDetailController}.
 *
 * Payment records are financially sensitive: amount, payment type, timestamps.
 * Income aggregation endpoints (monthly, yearly, total) expose business revenue
 * data. All endpoints must be restricted to the ADMIN role.
 *
 * Covered gaps (from ANALISI_SICUREZZA_2304):
 *  - Point 1 / Point 4 / Point 6: zero security coverage for this controller.
 */
@WebMvcTest(PaymentDetailController.class)
@Import({SecurityConfig.class, AppConfig.class})
@ActiveProfiles("test")
@DisplayName("PaymentDetailController – security tests")
class PaymentDetailControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;
    @MockBean private PaymentDetailsService paymentDetailsService;

    // ======================================================================
    //  Unauthenticated access — must return 401
    // ======================================================================

    @Nested
    @DisplayName("Unauthenticated access – must return 401")
    class UnauthenticatedAccess {

        @Test
        @DisplayName("GET /api/v1/paymentDetails returns 401 without token")
        void getAll_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/paymentDetails"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/paymentDetails/{id} returns 401 without token")
        void getById_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/paymentDetails/1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("PUT /api/v1/paymentDetails returns 401 without token")
        void update_noAuth_returns401() throws Exception {
            mockMvc.perform(put("/api/v1/paymentDetails")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{\"id\":1,\"amount\":100.0}"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/paymentDetails/monthlyIncome returns 401 without token")
        void monthlyIncome_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/paymentDetails/monthlyIncome"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/paymentDetails/yearlyIncome returns 401 without token")
        void yearlyIncome_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/paymentDetails/yearlyIncome"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/paymentDetails/totalIncome returns 401 without token")
        void totalIncome_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/paymentDetails/totalIncome"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/paymentDetails/filter returns 401 without token")
        void filter_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/paymentDetails/filter"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================================================================
    //  Non-ADMIN roles — must be forbidden with 403
    //  Income aggregation endpoints are particularly sensitive:
    //  exposing company revenue to CUSTOMER or EMPLOYEE is a data leak.
    // ======================================================================

    @Nested
    @DisplayName("CUSTOMER role – must be forbidden (403) on all endpoints")
    class CustomerRoleForbidden {

        @Test
        @DisplayName("GET /api/v1/paymentDetails returns 403 for CUSTOMER")
        @WithMockUser(roles = "CUSTOMER")
        void getAll_customerRole_returns403() throws Exception {
            mockMvc.perform(get("/api/v1/paymentDetails"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("GET /api/v1/paymentDetails/totalIncome returns 403 for CUSTOMER")
        @WithMockUser(roles = "CUSTOMER")
        void totalIncome_customerRole_returns403() throws Exception {
            // Company revenue data must not be visible to customers.
            mockMvc.perform(get("/api/v1/paymentDetails/totalIncome"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("GET /api/v1/paymentDetails/monthlyIncome returns 403 for CUSTOMER")
        @WithMockUser(roles = "CUSTOMER")
        void monthlyIncome_customerRole_returns403() throws Exception {
            mockMvc.perform(get("/api/v1/paymentDetails/monthlyIncome"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("PUT /api/v1/paymentDetails returns 403 for CUSTOMER")
        @WithMockUser(roles = "CUSTOMER")
        void update_customerRole_returns403() throws Exception {
            mockMvc.perform(put("/api/v1/paymentDetails")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{\"id\":1,\"amount\":100.0}"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("EMPLOYEE role – must be forbidden (403) on all endpoints")
    class EmployeeRoleForbidden {

        @Test
        @DisplayName("GET /api/v1/paymentDetails returns 403 for EMPLOYEE")
        @WithMockUser(roles = "EMPLOYEE")
        void getAll_employeeRole_returns403() throws Exception {
            mockMvc.perform(get("/api/v1/paymentDetails"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("GET /api/v1/paymentDetails/yearlyIncome returns 403 for EMPLOYEE")
        @WithMockUser(roles = "EMPLOYEE")
        void yearlyIncome_employeeRole_returns403() throws Exception {
            mockMvc.perform(get("/api/v1/paymentDetails/yearlyIncome"))
                    .andExpect(status().isForbidden());
        }
    }

    // ======================================================================
    //  ADMIN role — happy paths
    // ======================================================================

    @Nested
    @DisplayName("ADMIN role – must have full access")
    class AdminRoleAccess {

        @Test
        @DisplayName("GET /api/v1/paymentDetails succeeds for ADMIN")
        @WithMockUser(roles = "ADMIN")
        void getAll_adminRole_succeeds() throws Exception {
            when(paymentDetailsService.getAll()).thenReturn(List.of());
            mockMvc.perform(get("/api/v1/paymentDetails"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET /api/v1/paymentDetails/totalIncome succeeds for ADMIN")
        @WithMockUser(roles = "ADMIN")
        void totalIncome_adminRole_succeeds() throws Exception {
            when(paymentDetailsService.getTotalIncome()).thenReturn(9500.0);
            mockMvc.perform(get("/api/v1/paymentDetails/totalIncome"))
                    .andExpect(status().isOk());
        }
    }
}
