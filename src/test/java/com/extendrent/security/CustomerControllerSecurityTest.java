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
import src.controller.user.customer.CustomerController;
import src.core.config.AppConfig;
import src.core.config.SecurityConfig;
import src.core.security.JwtService;
import src.service.user.UserService;
import src.service.user.customer.CustomerService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security tests for {@link CustomerController}.
 *
 * CustomerController manages PII-sensitive customer records (name, email,
 * phone, driving license, rental history). All endpoints are restricted to
 * the ADMIN role: no customer self-service endpoints exist in this API.
 *
 * Covered gaps (from ANALISI_SICUREZZA_2304):
 *  - Point 1 / Point 4 / Point 6: zero security coverage for this controller.
 *  - Verifies both path-based (SecurityConfig) and method-level (@PreAuthorize) enforcement.
 */
@WebMvcTest(CustomerController.class)
@Import({SecurityConfig.class, AppConfig.class})
@ActiveProfiles("test")
@DisplayName("CustomerController – security tests")
class CustomerControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;
    @MockBean private CustomerService customerService;

    // ======================================================================
    //  Unauthenticated access — must be rejected with 401
    // ======================================================================

    @Nested
    @DisplayName("Unauthenticated access – must return 401")
    class UnauthenticatedAccess {

        @Test
        @DisplayName("GET /api/v1/customers returns 401 without token")
        void getAll_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/customers"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/customers/{id} returns 401 without token")
        void getById_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/customers/1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/customers/rentals/{customerId} returns 401 without token")
        void getRentalHistory_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/customers/rentals/1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("POST /api/v1/customers returns 401 without token")
        void create_noAuth_returns401() throws Exception {
            mockMvc.perform(post("/api/v1/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validCustomerJson()))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("PUT /api/v1/customers returns 401 without token")
        void update_noAuth_returns401() throws Exception {
            mockMvc.perform(put("/api/v1/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("DELETE /api/v1/customers returns 401 without token")
        void delete_noAuth_returns401() throws Exception {
            mockMvc.perform(delete("/api/v1/customers")
                            .param("id", "1")
                            .param("isHardDelete", "false"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/customers/count/{isDeleted} returns 401 without token")
        void count_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/customers/count/false"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/customers/countByStatus/{status} returns 401 without token")
        void countByStatus_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/customers/countByStatus/VERIFIED"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================================================================
    //  Non-ADMIN roles — must be forbidden with 403
    //  These tests protect against privilege escalation by CUSTOMER or EMPLOYEE
    // ======================================================================

    @Nested
    @DisplayName("CUSTOMER role – must be forbidden (403) on all endpoints")
    class CustomerRoleForbidden {

        @Test
        @DisplayName("GET /api/v1/customers returns 403 for CUSTOMER role")
        @WithMockUser(roles = "CUSTOMER")
        void getAll_customerRole_returns403() throws Exception {
            mockMvc.perform(get("/api/v1/customers"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("GET /api/v1/customers/{id} returns 403 for CUSTOMER role")
        @WithMockUser(roles = "CUSTOMER")
        void getById_customerRole_returns403() throws Exception {
            mockMvc.perform(get("/api/v1/customers/1"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("GET /api/v1/customers/rentals/{id} returns 403 for CUSTOMER role")
        @WithMockUser(roles = "CUSTOMER")
        void getRentalHistory_customerRole_returns403() throws Exception {
            // CUSTOMER cannot view any rental history via this endpoint —
            // this is ADMIN reporting, not a self-service endpoint.
            mockMvc.perform(get("/api/v1/customers/rentals/1"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("POST /api/v1/customers returns 403 for CUSTOMER role")
        @WithMockUser(roles = "CUSTOMER")
        void create_customerRole_returns403() throws Exception {
            mockMvc.perform(post("/api/v1/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validCustomerJson()))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("DELETE /api/v1/customers (hard delete) returns 403 for CUSTOMER role")
        @WithMockUser(roles = "CUSTOMER")
        void hardDelete_customerRole_returns403() throws Exception {
            mockMvc.perform(delete("/api/v1/customers")
                            .param("id", "1")
                            .param("isHardDelete", "true"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("EMPLOYEE role – must be forbidden (403) on all endpoints")
    class EmployeeRoleForbidden {

        @Test
        @DisplayName("GET /api/v1/customers returns 403 for EMPLOYEE role")
        @WithMockUser(roles = "EMPLOYEE")
        void getAll_employeeRole_returns403() throws Exception {
            mockMvc.perform(get("/api/v1/customers"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("DELETE /api/v1/customers returns 403 for EMPLOYEE role")
        @WithMockUser(roles = "EMPLOYEE")
        void delete_employeeRole_returns403() throws Exception {
            mockMvc.perform(delete("/api/v1/customers")
                            .param("id", "1")
                            .param("isHardDelete", "false"))
                    .andExpect(status().isForbidden());
        }
    }

    // ======================================================================
    //  ADMIN role — happy paths (full access)
    // ======================================================================

    @Nested
    @DisplayName("ADMIN role – must have full access")
    class AdminRoleAccess {

        @Test
        @DisplayName("GET /api/v1/customers succeeds for ADMIN role")
        @WithMockUser(roles = "ADMIN")
        void getAll_adminRole_succeeds() throws Exception {
            when(customerService.getAll()).thenReturn(List.of());
            mockMvc.perform(get("/api/v1/customers"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET /api/v1/customers/count/false succeeds for ADMIN role")
        @WithMockUser(roles = "ADMIN")
        void count_adminRole_succeeds() throws Exception {
            when(customerService.getCountByDeletedState(false)).thenReturn(10);
            mockMvc.perform(get("/api/v1/customers/count/false"))
                    .andExpect(status().isOk());
        }
    }

    // ---- helpers -----------------------------------------------------------

    private static String validCustomerJson() {
        return """
                {"name":"Mario","surname":"Rossi","emailAddress":"mario@example.com",
                "password":"Password1!","phoneNumber":"3331234567",
                "drivingLicenseNumber":"AB1234","drivingLicenseTypeEntityId":1,
                "userImageEntityId":1}""";
    }
}
