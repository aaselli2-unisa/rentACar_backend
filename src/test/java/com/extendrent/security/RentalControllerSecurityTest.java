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
import src.controller.rental.RentalController;
import src.core.config.AppConfig;
import src.core.config.SecurityConfig;
import src.core.security.JwtService;
import src.service.rental.RentalService;
import src.service.rental.status.RentalStatusService;
import src.service.user.UserService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security tests for {@link RentalController}.
 *
 * Rental data is business-critical and financially sensitive:
 * - Customer PII (who rented what, when)
 * - Financial records (payment amounts, rental prices)
 * - Operational state (which cars are currently rented)
 *
 * All endpoints must require authentication; destructive operations (delete,
 * state changes) must be restricted to appropriate roles.
 */
@WebMvcTest(RentalController.class)
@Import({SecurityConfig.class, AppConfig.class})
@ActiveProfiles("test")
@DisplayName("RentalController – security tests")
class RentalControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;
    @MockBean private RentalService rentalService;
    @MockBean private RentalStatusService rentalStatusService;

    // ======================================================================
    //  Unauthenticated access – must be rejected
    // ======================================================================

    @Nested
    @DisplayName("Unauthenticated access – MUST be rejected (401)")
    class UnauthenticatedAccess {

        @Test
        @DisplayName("GET /api/v1/rentals must return 401 without a token")
        void listAllRentals_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/rentals"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/rentals/{id} must return 401 without a token")
        void getRentalById_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/rentals/1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("POST /api/v1/rentals must return 401 without a token")
        void createRental_noAuth_returns401() throws Exception {
            mockMvc.perform(post("/api/v1/rentals")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("PUT /api/v1/rentals must return 401 without a token")
        void updateRental_noAuth_returns401() throws Exception {
            mockMvc.perform(put("/api/v1/rentals")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("DELETE /api/v1/rentals must return 401 without a token")
        void deleteRental_noAuth_returns401() throws Exception {
            mockMvc.perform(delete("/api/v1/rentals")
                            .param("id", "1")
                            .param("isHardDelete", "false"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("PUT /api/v1/rentals/startRental/{id} must return 401")
        void startRental_noAuth_returns401() throws Exception {
            mockMvc.perform(put("/api/v1/rentals/startRental/1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("PUT /api/v1/rentals/returnRental must return 401")
        void returnRental_noAuth_returns401() throws Exception {
            mockMvc.perform(put("/api/v1/rentals/returnRental")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("PUT /api/v1/rentals/cancelRental/{id} must return 401")
        void cancelRental_noAuth_returns401() throws Exception {
            mockMvc.perform(put("/api/v1/rentals/cancelRental/1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("POST /api/v1/rentals/showRental must return 401")
        void showRental_noAuth_returns401() throws Exception {
            mockMvc.perform(post("/api/v1/rentals/showRental")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/rentals (isDeleted filter) must return 401")
        void rentalsByDeletedState_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/rentals").param("isDeleted", "false"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/rentals (statusId filter) must return 401")
        void rentalsByStatus_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/rentals").param("statusId", "1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/rentals/count/{isDeleted} must return 401")
        void rentalCount_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/rentals/count/false"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/rentals/countByStatus/{status} must return 401")
        void rentalCountByStatus_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/rentals/countByStatus/1"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================================================================
    //  Role-based access – CUSTOMER should not be able to hard-delete
    // ======================================================================

    @Nested
    @DisplayName("CUSTOMER role – restricted write operations")
    class CustomerRoleRestrictions {

        @Test
        @DisplayName("DELETE /api/v1/rentals must return 403 for CUSTOMER")
        @WithMockUser(roles = "CUSTOMER")
        void deleteRental_customerRole_returns403() throws Exception {
            mockMvc.perform(delete("/api/v1/rentals")
                            .param("id", "1")
                            .param("isHardDelete", "true"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("PUT /api/v1/rentals/startRental must return 403 for CUSTOMER")
        @WithMockUser(roles = "CUSTOMER")
        void startRental_customerRole_returns403() throws Exception {
            // Starting a rental is an EMPLOYEE/ADMIN operation, not a customer action
            mockMvc.perform(put("/api/v1/rentals/startRental/1"))
                    .andExpect(status().isForbidden());
        }
    }

    // ======================================================================
    //  Business-logic access control – customers own their rentals
    // ======================================================================

    @Nested
    @DisplayName("Rental read access is restricted for CUSTOMER by security policy")
    class RentalOwnershipCheck {

        @Test
        @DisplayName("CUSTOMER cannot list all rentals (ADMIN-only endpoint)")
        @WithMockUser(username = "customer1@example.com", roles = "CUSTOMER")
        void listAllRentals_customerRole_returns403() throws Exception {
            // Current hardening uses role-based restriction: rentals listing is ADMIN-only.
            when(rentalService.getAll()).thenReturn(List.of());
            mockMvc.perform(get("/api/v1/rentals"))
                    .andExpect(status().isForbidden());
        }
    }

    // ======================================================================
    //  Authenticated happy paths (ADMIN)
    // ======================================================================

    @Nested
    @DisplayName("ADMIN role – expected happy paths")
    class AdminHappyPath {

        @Test
        @DisplayName("GET /api/v1/rentals succeeds for ADMIN")
        @WithMockUser(roles = "ADMIN")
        void listAllRentals_adminRole_succeeds() throws Exception {
            when(rentalService.getAll()).thenReturn(List.of());
            mockMvc.perform(get("/api/v1/rentals"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET /api/v1/rentals/statuses is accessible to ADMIN")
        @WithMockUser(roles = "ADMIN")
        void getRentalStatuses_adminRole_succeeds() throws Exception {
            when(rentalStatusService.getAll()).thenReturn(List.of());
            mockMvc.perform(get("/api/v1/rentals/statuses"))
                    .andExpect(status().isOk());
        }
    }
}
