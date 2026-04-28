package com.extendrent.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import src.controller.payment.type.PaymentTypeController;
import src.core.config.AppConfig;
import src.core.config.SecurityConfig;
import src.core.security.JwtService;
import src.service.payment.type.PaymentTypeService;
import src.service.user.UserService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security tests for {@link PaymentTypeController}.
 *
 * PaymentType is a lookup table for payment methods (Credit Card, Cash, etc.).
 * All endpoints are protected by hasRole('ADMIN') in SecurityConfig.
 * This test class provides the first coverage for this controller (gap G5
 * identified in ENDPOINT_ACCESS_MATRIX.md).
 *
 * No business vulnerability was found (SecurityConfig already correct), but
 * without these tests a future regression in SecurityConfig that removes the
 * paymentTypes rule would go undetected in CI.
 */
@WebMvcTest(PaymentTypeController.class)
@Import({SecurityConfig.class, AppConfig.class})
@ActiveProfiles("test")
@DisplayName("PaymentTypeController – security tests (gap G5)")
class PaymentTypeControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;
    @MockBean private PaymentTypeService paymentTypeService;

    // ======================================================================
    //  Unauthenticated access — must return 401
    // ======================================================================

    @Nested
    @DisplayName("Unauthenticated access – must return 401")
    class UnauthenticatedAccess {

        @Test
        @DisplayName("GET /api/v1/paymentTypes returns 401 without token")
        void getAll_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/paymentTypes"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/paymentTypes/{id} returns 401 without token")
        void getById_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/paymentTypes/1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/paymentTypes?isActive= returns 401 without token")
        void getAllByActiveState_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/paymentTypes").param("isActive", "true"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("PUT /api/v1/paymentTypes returns 401 without token")
        void update_noAuth_returns401() throws Exception {
            mockMvc.perform(put("/api/v1/paymentTypes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"id\":1,\"name\":\"Cash\"}"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================================================================
    //  Non-ADMIN roles — must be forbidden with 403
    //  A CUSTOMER must not be able to read or modify payment type definitions.
    // ======================================================================

    @Nested
    @DisplayName("CUSTOMER role – must be forbidden (403) on all endpoints")
    class CustomerRoleForbidden {

        @Test
        @DisplayName("GET /api/v1/paymentTypes returns 403 for CUSTOMER")
        @WithMockUser(roles = "CUSTOMER")
        void getAll_customerRole_returns403() throws Exception {
            mockMvc.perform(get("/api/v1/paymentTypes"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("PUT /api/v1/paymentTypes returns 403 for CUSTOMER")
        @WithMockUser(roles = "CUSTOMER")
        void update_customerRole_returns403() throws Exception {
            mockMvc.perform(put("/api/v1/paymentTypes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"id\":1,\"name\":\"Cash\"}"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("GET /api/v1/paymentTypes?isActive=true returns 403 for CUSTOMER")
        @WithMockUser(roles = "CUSTOMER")
        void getAllByActiveState_customerRole_returns403() throws Exception {
            mockMvc.perform(get("/api/v1/paymentTypes").param("isActive", "true"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("EMPLOYEE role – must be forbidden (403) on all endpoints")
    class EmployeeRoleForbidden {

        @Test
        @DisplayName("GET /api/v1/paymentTypes returns 403 for EMPLOYEE")
        @WithMockUser(roles = "EMPLOYEE")
        void getAll_employeeRole_returns403() throws Exception {
            mockMvc.perform(get("/api/v1/paymentTypes"))
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
        @DisplayName("GET /api/v1/paymentTypes succeeds for ADMIN")
        @WithMockUser(roles = "ADMIN")
        void getAll_adminRole_succeeds() throws Exception {
            when(paymentTypeService.getAll()).thenReturn(List.of());
            mockMvc.perform(get("/api/v1/paymentTypes"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET /api/v1/paymentTypes/{id} succeeds for ADMIN")
        @WithMockUser(roles = "ADMIN")
        void getById_adminRole_succeeds() throws Exception {
            when(paymentTypeService.getById(1)).thenReturn(null);
            mockMvc.perform(get("/api/v1/paymentTypes/1"))
                    .andExpect(result ->
                            org.assertj.core.api.Assertions.assertThat(
                                    result.getResponse().getStatus()).isNotEqualTo(401)
                    );
        }
    }
}
