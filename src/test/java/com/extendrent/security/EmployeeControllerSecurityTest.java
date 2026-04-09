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
import src.controller.user.employee.EmployeeController;
import src.core.config.AppConfig;
import src.core.config.SecurityConfig;
import src.core.security.JwtService;
import src.service.user.UserService;
import src.service.user.employee.EmployeeService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security tests for {@link EmployeeController}.
 *
 * Employee data (salary, phone, personal info) is sensitive PII.
 * The salary-range filter in particular enables harvesting compensation data
 * without any authentication — a significant privacy and business-intelligence risk.
 */
@WebMvcTest(EmployeeController.class)
@Import({SecurityConfig.class, AppConfig.class})
@ActiveProfiles("test")
@DisplayName("EmployeeController – security tests")
class EmployeeControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;
    @MockBean private EmployeeService employeeService;

    // ======================================================================
    //  Unauthenticated access – must be rejected
    // ======================================================================

    @Nested
    @DisplayName("Unauthenticated access – MUST be rejected (401)")
    class UnauthenticatedAccess {

        @Test
        @DisplayName("GET /api/v1/employees must return 401 without token – FAILS until fixed")
        void listEmployees_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/employees"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/employees/{id} must return 401 without token – FAILS until fixed")
        void getEmployeeById_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/employees/1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("POST /api/v1/employees must return 401 without token – FAILS until fixed")
        void createEmployee_noAuth_returns401() throws Exception {
            mockMvc.perform(post("/api/v1/employees")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("PUT /api/v1/employees must return 401 without token – FAILS until fixed")
        void updateEmployee_noAuth_returns401() throws Exception {
            mockMvc.perform(put("/api/v1/employees")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("DELETE /api/v1/employees must return 401 without token – FAILS until fixed")
        void deleteEmployee_noAuth_returns401() throws Exception {
            mockMvc.perform(delete("/api/v1/employees")
                            .param("id", "1")
                            .param("isHardDelete", "false"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("VULNERABILITY: Salary range filter exposes salary PII without auth – FAILS until fixed")
        void salaryRange_noAuth_exposesSensitivePii() throws Exception {
            // An unauthenticated attacker can enumerate all employees and their salary ranges.
            // This is a severe PII and business-intelligence leak.
            mockMvc.perform(get("/api/v1/employees")
                            .param("startSalary", "0")
                            .param("endSalary", "1000000"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("VULNERABILITY: Phone number lookup by phone exposes PII without auth – FAILS until fixed")
        void phoneNumberLookup_noAuth_exposesPii() throws Exception {
            mockMvc.perform(get("/api/v1/employees/phone/5551234567"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================================================================
    //  CUSTOMER role – must NOT access employee management
    // ======================================================================

    @Nested
    @DisplayName("CUSTOMER role – must be forbidden on employee management endpoints")
    class CustomerRoleAccess {

        @Test
        @DisplayName("GET /api/v1/employees must return 403 for CUSTOMER – FAILS until fixed")
        @WithMockUser(roles = "CUSTOMER")
        void listEmployees_customerRole_returns403() throws Exception {
            mockMvc.perform(get("/api/v1/employees"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("GET /api/v1/employees (salary filter) must return 403 for CUSTOMER – FAILS until fixed")
        @WithMockUser(roles = "CUSTOMER")
        void salaryFilter_customerRole_returns403() throws Exception {
            mockMvc.perform(get("/api/v1/employees")
                            .param("startSalary", "0")
                            .param("endSalary", "200000"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("POST /api/v1/employees must return 403 for CUSTOMER – FAILS until fixed")
        @WithMockUser(roles = "CUSTOMER")
        void createEmployee_customerRole_returns403() throws Exception {
            mockMvc.perform(post("/api/v1/employees")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isForbidden());
        }
    }

    // ======================================================================
    //  ADMIN role – should have access (happy path after fix)
    // ======================================================================

    @Nested
    @DisplayName("ADMIN role – should have full access after security fix")
    class AdminRoleAccess {

        @Test
        @DisplayName("GET /api/v1/employees succeeds for ADMIN")
        @WithMockUser(roles = "ADMIN")
        void listEmployees_adminRole_succeeds() throws Exception {
            when(employeeService.getAll()).thenReturn(List.of());
            mockMvc.perform(get("/api/v1/employees"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET /api/v1/employees (salary filter) succeeds for ADMIN")
        @WithMockUser(roles = "ADMIN")
        void salaryFilter_adminRole_succeeds() throws Exception {
            when(employeeService.getAllBySalaryBetween(0.0, 200000.0)).thenReturn(List.of());
            mockMvc.perform(get("/api/v1/employees")
                            .param("startSalary", "0")
                            .param("endSalary", "200000"))
                    .andExpect(status().isOk());
        }
    }
}
