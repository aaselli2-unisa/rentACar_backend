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
import src.controller.discount.DiscountController;
import src.core.config.AppConfig;
import src.core.config.SecurityConfig;
import src.core.security.JwtService;
import src.service.discount.DiscountService;
import src.service.user.UserService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security tests for {@link DiscountController}.
 *
 * Discount codes affect pricing (up to 90% off). Unauthorized access allows:
 *  - Creating unlimited discount codes → revenue loss
 *  - Discovering active discount codes → unfair pricing advantage
 *  - Deleting discount codes → service disruption
 *
 * All endpoints must be restricted to the ADMIN role.
 *
 * Covered gaps (from ANALISI_SICUREZZA_2304): Point 4 — no security tests.
 */
@WebMvcTest(DiscountController.class)
@Import({SecurityConfig.class, AppConfig.class})
@ActiveProfiles("test")
@DisplayName("DiscountController – security tests")
class DiscountControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;
    @MockBean private DiscountService discountService;

    // ======================================================================
    //  Unauthenticated access — must return 401
    // ======================================================================

    @Nested
    @DisplayName("Unauthenticated access – must return 401")
    class UnauthenticatedAccess {

        @Test
        @DisplayName("GET /api/v1/discounts returns 401 without token")
        void getAll_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/discounts"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/discounts/{id} returns 401 without token")
        void getById_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/discounts/1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/discounts/code/{code} returns 401 without token")
        void getByCode_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/discounts/code/SUMMER20"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("POST /api/v1/discounts returns 401 without token")
        void create_noAuth_returns401() throws Exception {
            mockMvc.perform(post("/api/v1/discounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validDiscountJson()))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("PUT /api/v1/discounts returns 401 without token")
        void update_noAuth_returns401() throws Exception {
            mockMvc.perform(put("/api/v1/discounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("DELETE /api/v1/discounts returns 401 without token")
        void delete_noAuth_returns401() throws Exception {
            mockMvc.perform(delete("/api/v1/discounts")
                            .param("id", "1")
                            .param("isHardDelete", "false"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================================================================
    //  Non-ADMIN roles — must be forbidden with 403
    //  A CUSTOMER discovering discount codes or a EMPLOYEE creating them
    //  are both unauthorized business operations.
    // ======================================================================

    @Nested
    @DisplayName("CUSTOMER role – must be forbidden (403)")
    class CustomerRoleForbidden {

        @Test
        @DisplayName("GET /api/v1/discounts returns 403 for CUSTOMER")
        @WithMockUser(roles = "CUSTOMER")
        void getAll_customerRole_returns403() throws Exception {
            // Listing all discount codes would expose active promotional codes.
            mockMvc.perform(get("/api/v1/discounts"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("GET /api/v1/discounts/code/{code} returns 403 for CUSTOMER")
        @WithMockUser(roles = "CUSTOMER")
        void getByCode_customerRole_returns403() throws Exception {
            mockMvc.perform(get("/api/v1/discounts/code/SUMMER20"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("POST /api/v1/discounts returns 403 for CUSTOMER")
        @WithMockUser(roles = "CUSTOMER")
        void create_customerRole_returns403() throws Exception {
            mockMvc.perform(post("/api/v1/discounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validDiscountJson()))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("DELETE /api/v1/discounts returns 403 for CUSTOMER")
        @WithMockUser(roles = "CUSTOMER")
        void delete_customerRole_returns403() throws Exception {
            mockMvc.perform(delete("/api/v1/discounts")
                            .param("id", "1")
                            .param("isHardDelete", "true"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("EMPLOYEE role – must be forbidden (403)")
    class EmployeeRoleForbidden {

        @Test
        @DisplayName("POST /api/v1/discounts returns 403 for EMPLOYEE")
        @WithMockUser(roles = "EMPLOYEE")
        void create_employeeRole_returns403() throws Exception {
            mockMvc.perform(post("/api/v1/discounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validDiscountJson()))
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
        @DisplayName("GET /api/v1/discounts succeeds for ADMIN")
        @WithMockUser(roles = "ADMIN")
        void getAll_adminRole_succeeds() throws Exception {
            when(discountService.getAll()).thenReturn(List.of());
            mockMvc.perform(get("/api/v1/discounts"))
                    .andExpect(status().isOk());
        }
    }

    // ---- helpers -----------------------------------------------------------

    private static String validDiscountJson() {
        return """
                {"discountCode":"SUMMER20","discountPercentage":20,"isActive":true}""";
    }
}
