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
import src.controller.vehicle.features.common.brand.BrandController;
import src.core.config.AppConfig;
import src.core.config.SecurityConfig;
import src.core.security.JwtService;
import src.service.user.UserService;
import src.service.vehicle.features.common.brand.BrandService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security tests for lookup/catalogue controllers: Brand (representative sample).
 *
 * Brand, Color, Fuel, Shift, VehicleStatus, CarBodyType, CarModel, CarSegment
 * all share the same security policy: /api/v1/{domain}/** → authenticated().
 * Any authenticated user (CUSTOMER, EMPLOYEE, ADMIN) may read these lookup
 * tables. Write/delete operations are also authenticated (not ADMIN-only) —
 * this is the current policy; a future hardening pass may tighten to ADMIN.
 *
 * This test uses BrandController as a representative sample. The underlying
 * security rule (authenticated()) applies identically to all lookup controllers.
 *
 * Covered gaps (from ANALISI_SICUREZZA_2304): Point 4 — no security tests.
 */
@WebMvcTest(BrandController.class)
@Import({SecurityConfig.class, AppConfig.class})
@ActiveProfiles("test")
@DisplayName("Lookup controllers (Brand sample) – security tests")
class LookupControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;
    @MockBean private BrandService brandService;

    // ======================================================================
    //  Unauthenticated access — must return 401
    // ======================================================================

    @Nested
    @DisplayName("Unauthenticated access – must return 401")
    class UnauthenticatedAccess {

        @Test
        @DisplayName("GET /api/v1/brands returns 401 without token")
        void getAll_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/brands"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/brands/{id} returns 401 without token")
        void getById_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/brands/1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("POST /api/v1/brands returns 401 without token")
        void create_noAuth_returns401() throws Exception {
            mockMvc.perform(post("/api/v1/brands")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Toyota\"}"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("PUT /api/v1/brands returns 401 without token")
        void update_noAuth_returns401() throws Exception {
            mockMvc.perform(put("/api/v1/brands")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{\"id\":1,\"name\":\"Honda\"}"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("DELETE /api/v1/brands returns 401 without token")
        void delete_noAuth_returns401() throws Exception {
            mockMvc.perform(delete("/api/v1/brands")
                            .param("id", "1")
                            .param("isHardDelete", "false"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================================================================
    //  Authenticated access — any role can access lookup data
    //  (current policy: authenticated(), not role-restricted)
    // ======================================================================

    @Nested
    @DisplayName("Any authenticated role – allowed to read brand catalogue")
    class AuthenticatedReadAccess {

        @Test
        @DisplayName("GET /api/v1/brands succeeds for CUSTOMER role")
        @WithMockUser(roles = "CUSTOMER")
        void getAll_customerRole_succeeds() throws Exception {
            when(brandService.getAll()).thenReturn(List.of());
            mockMvc.perform(get("/api/v1/brands"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET /api/v1/brands succeeds for EMPLOYEE role")
        @WithMockUser(roles = "EMPLOYEE")
        void getAll_employeeRole_succeeds() throws Exception {
            when(brandService.getAll()).thenReturn(List.of());
            mockMvc.perform(get("/api/v1/brands"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET /api/v1/brands succeeds for ADMIN role")
        @WithMockUser(roles = "ADMIN")
        void getAll_adminRole_succeeds() throws Exception {
            when(brandService.getAll()).thenReturn(List.of());
            mockMvc.perform(get("/api/v1/brands"))
                    .andExpect(status().isOk());
        }
    }

    // ======================================================================
    //  Write operations — must be restricted to ADMIN (Security patch V16)
    //
    //  All lookup controllers (Brand, Color, Fuel, GearShift, VehicleStatus,
    //  CarBodyType, CarModel, CarSegment) share the same security policy.
    //  BrandController is used as representative sample. Prior to V16,
    //  POST/PUT/DELETE only required authenticated() — any CUSTOMER could
    //  delete a brand or add a color. V16 restricts write ops to ADMIN.
    // ======================================================================

    @Nested
    @DisplayName("Write operations – CUSTOMER must be forbidden (403) — V16 / CWE-284")
    class WriteOpsForbiddenForCustomer {

        @Test
        @DisplayName("POST /api/v1/brands returns 403 for CUSTOMER (cannot create lookup entries)")
        @WithMockUser(roles = "CUSTOMER")
        void createBrand_customerRole_returns403() throws Exception {
            mockMvc.perform(post("/api/v1/brands")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Toyota\"}"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("PUT /api/v1/brands returns 403 for CUSTOMER (cannot update lookup entries)")
        @WithMockUser(roles = "CUSTOMER")
        void updateBrand_customerRole_returns403() throws Exception {
            mockMvc.perform(put("/api/v1/brands")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{\"id\":1,\"name\":\"Honda\"}"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("DELETE /api/v1/brands returns 403 for CUSTOMER (cannot delete lookup entries)")
        @WithMockUser(roles = "CUSTOMER")
        void deleteBrand_customerRole_returns403() throws Exception {
            mockMvc.perform(delete("/api/v1/brands")
                            .param("id", "1")
                            .param("isHardDelete", "false"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("POST /api/v1/brands returns 403 for EMPLOYEE (write restricted to ADMIN)")
        @WithMockUser(roles = "EMPLOYEE")
        void createBrand_employeeRole_returns403() throws Exception {
            mockMvc.perform(post("/api/v1/brands")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Ford\"}"))
                    .andExpect(status().isForbidden());
        }
    }
}
