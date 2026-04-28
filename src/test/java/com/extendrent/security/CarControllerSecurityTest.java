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
import src.controller.vehicle.car.CarController;
import src.core.config.AppConfig;
import src.core.config.SecurityConfig;
import src.core.security.JwtService;
import src.service.user.UserService;
import src.service.vehicle.car.CarService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security tests for {@link CarController}.
 *
 * Car catalogue data (availability, pricing, specs) is accessible to all
 * authenticated users per the current security policy: /api/v1/cars/** → authenticated().
 * This is intentional for browsing. However, write operations (create, update,
 * delete) are also authenticated-only, not ADMIN-only.
 *
 * NOTE: Consider restricting POST/PUT/DELETE on /api/v1/cars/** to ADMIN role
 * in a future hardening pass, as customers should not be able to modify the car catalogue.
 *
 * These tests document and verify the CURRENT policy.
 *
 * Covered gaps (from ANALISI_SICUREZZA_2304): Point 4 — no security tests.
 */
@WebMvcTest(CarController.class)
@Import({SecurityConfig.class, AppConfig.class})
@ActiveProfiles("test")
@DisplayName("CarController – security tests")
class CarControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;
    @MockBean private CarService carService;

    // ======================================================================
    //  Unauthenticated access — must return 401 for all endpoints
    // ======================================================================

    @Nested
    @DisplayName("Unauthenticated access – must return 401")
    class UnauthenticatedAccess {

        @Test
        @DisplayName("GET /api/v1/cars returns 401 without token")
        void getAll_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/cars"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/cars/{id} returns 401 without token")
        void getById_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/cars/1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("POST /api/v1/cars returns 401 without token")
        void create_noAuth_returns401() throws Exception {
            mockMvc.perform(post("/api/v1/cars")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("PUT /api/v1/cars returns 401 without token")
        void update_noAuth_returns401() throws Exception {
            mockMvc.perform(put("/api/v1/cars")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("DELETE /api/v1/cars returns 401 without token")
        void delete_noAuth_returns401() throws Exception {
            mockMvc.perform(delete("/api/v1/cars")
                            .param("id", "1")
                            .param("isHardDelete", "false"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/cars/filter returns 401 without token")
        void filter_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/cars/filter"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/cars/count/{isDeleted} returns 401 without token")
        void count_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/cars/count/false"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================================================================
    //  Authenticated access — any role can read car catalogue data
    // ======================================================================

    @Nested
    @DisplayName("Any authenticated role – read access to car catalogue")
    class AuthenticatedReadAccess {

        @Test
        @DisplayName("GET /api/v1/cars succeeds for CUSTOMER role")
        @WithMockUser(roles = "CUSTOMER")
        void getAll_customerRole_succeeds() throws Exception {
            when(carService.getAll()).thenReturn(List.of());
            mockMvc.perform(get("/api/v1/cars"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET /api/v1/cars succeeds for EMPLOYEE role")
        @WithMockUser(roles = "EMPLOYEE")
        void getAll_employeeRole_succeeds() throws Exception {
            when(carService.getAll()).thenReturn(List.of());
            mockMvc.perform(get("/api/v1/cars"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET /api/v1/cars succeeds for ADMIN role")
        @WithMockUser(roles = "ADMIN")
        void getAll_adminRole_succeeds() throws Exception {
            when(carService.getAll()).thenReturn(List.of());
            mockMvc.perform(get("/api/v1/cars"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET /api/v1/cars/count/false succeeds for CUSTOMER role")
        @WithMockUser(roles = "CUSTOMER")
        void count_customerRole_succeeds() throws Exception {
            when(carService.getCountByDeletedState(false)).thenReturn(5);
            mockMvc.perform(get("/api/v1/cars/count/false"))
                    .andExpect(status().isOk());
        }
    }

    // ======================================================================
    //  Write operations — must be restricted to ADMIN (Security patch V16)
    //
    //  Prior to V16, POST/PUT/DELETE on /api/v1/cars/** only required
    //  authenticated(). Any CUSTOMER could create, update, or delete vehicles.
    //  These tests document that V16 fixes the privilege escalation (G1).
    // ======================================================================

    @Nested
    @DisplayName("Write operations – CUSTOMER must be forbidden (403) — V16 / CWE-284")
    class WriteOpsForbiddenForCustomer {

        @Test
        @DisplayName("POST /api/v1/cars returns 403 for CUSTOMER (cannot create vehicles)")
        @WithMockUser(roles = "CUSTOMER")
        void createCar_customerRole_returns403() throws Exception {
            mockMvc.perform(post("/api/v1/cars")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("PUT /api/v1/cars returns 403 for CUSTOMER (cannot update vehicles)")
        @WithMockUser(roles = "CUSTOMER")
        void updateCar_customerRole_returns403() throws Exception {
            mockMvc.perform(put("/api/v1/cars")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("DELETE /api/v1/cars returns 403 for CUSTOMER (cannot delete vehicles)")
        @WithMockUser(roles = "CUSTOMER")
        void deleteCar_customerRole_returns403() throws Exception {
            mockMvc.perform(delete("/api/v1/cars")
                            .param("id", "1")
                            .param("isHardDelete", "false"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("POST /api/v1/cars returns 403 for EMPLOYEE (write restricted to ADMIN)")
        @WithMockUser(roles = "EMPLOYEE")
        void createCar_employeeRole_returns403() throws Exception {
            mockMvc.perform(post("/api/v1/cars")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isForbidden());
        }
    }
}
