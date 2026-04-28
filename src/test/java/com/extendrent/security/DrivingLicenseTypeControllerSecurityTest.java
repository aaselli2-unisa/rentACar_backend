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
import src.controller.license.DrivingLicenseTypeController;
import src.core.config.AppConfig;
import src.core.config.SecurityConfig;
import src.core.security.JwtService;
import src.service.license.DrivingLicenseTypeService;
import src.service.user.UserService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security tests for {@link DrivingLicenseTypeController}.
 *
 * DrivingLicenseType has a MIXED security policy (SecurityConfig V16):
 *   - GET /api/v1/drivingLicenseType/** → permitAll()   (needed by the signup form dropdown)
 *   - POST/PUT/DELETE                   → hasRole('ADMIN')
 *
 * This is the only controller in the application where GET is fully public
 * (no authentication required). All write operations are ADMIN-only.
 *
 * Covered gap: G3 — DrivingLicenseType missing from test suite.
 * Mixed-policy controllers are the highest-risk for regressions: a single
 * SecurityConfig edit could accidentally flip GET back to authenticated()
 * (breaking the signup form) or open write ops to unauthenticated access.
 */
@WebMvcTest(DrivingLicenseTypeController.class)
@Import({SecurityConfig.class, AppConfig.class})
@ActiveProfiles("test")
@DisplayName("DrivingLicenseTypeController – security tests (gap G3, mixed policy)")
class DrivingLicenseTypeControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;
    @MockBean private DrivingLicenseTypeService drivingLicenseTypeService;

    // ======================================================================
    //  GET endpoints — PUBLIC (permitAll): no authentication required
    //  This is intentional: the signup form needs the dropdown without login.
    // ======================================================================

    @Nested
    @DisplayName("GET endpoints – public (permitAll) – no auth needed")
    class PublicGetAccess {

        @Test
        @DisplayName("GET /api/v1/drivingLicenseType returns 200 without any token")
        void getAll_noAuth_returns200() throws Exception {
            when(drivingLicenseTypeService.getAll()).thenReturn(List.of());
            mockMvc.perform(get("/api/v1/drivingLicenseType"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET /api/v1/drivingLicenseType/{id} returns 200 without any token")
        void getById_noAuth_returns200() throws Exception {
            when(drivingLicenseTypeService.getById(1)).thenReturn(null);
            mockMvc.perform(get("/api/v1/drivingLicenseType/1"))
                    .andExpect(result ->
                            org.assertj.core.api.Assertions.assertThat(
                                    result.getResponse().getStatus()).isNotEqualTo(401)
                    );
        }

        @Test
        @DisplayName("GET /api/v1/drivingLicenseType?isDeleted=false returns 200 without any token")
        void getAllByDeletedState_noAuth_returns200() throws Exception {
            when(drivingLicenseTypeService.getAllByDeletedState(false)).thenReturn(List.of());
            mockMvc.perform(get("/api/v1/drivingLicenseType").param("isDeleted", "false"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET /api/v1/drivingLicenseType returns 200 for CUSTOMER (public endpoint)")
        @WithMockUser(roles = "CUSTOMER")
        void getAll_customerRole_returns200() throws Exception {
            when(drivingLicenseTypeService.getAll()).thenReturn(List.of());
            mockMvc.perform(get("/api/v1/drivingLicenseType"))
                    .andExpect(status().isOk());
        }
    }

    // ======================================================================
    //  Write ops — anonymous: must return 401
    //  Even though GET is public, unauthenticated write attempts must be blocked.
    // ======================================================================

    @Nested
    @DisplayName("Write ops – unauthenticated access must return 401")
    class WriteOpsUnauthenticated {

        @Test
        @DisplayName("POST /api/v1/drivingLicenseType returns 401 without token")
        void create_noAuth_returns401() throws Exception {
            mockMvc.perform(post("/api/v1/drivingLicenseType")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"B\"}"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("PUT /api/v1/drivingLicenseType returns 401 without token")
        void update_noAuth_returns401() throws Exception {
            mockMvc.perform(put("/api/v1/drivingLicenseType")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"id\":1,\"name\":\"B\"}"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("DELETE /api/v1/drivingLicenseType returns 401 without token")
        void delete_noAuth_returns401() throws Exception {
            mockMvc.perform(delete("/api/v1/drivingLicenseType")
                            .param("id", "1")
                            .param("isHardDelete", "false"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================================================================
    //  Write ops — CUSTOMER/EMPLOYEE: must return 403
    //  Privilege escalation guard: only ADMIN may manage license type catalogue.
    // ======================================================================

    @Nested
    @DisplayName("Write ops – CUSTOMER/EMPLOYEE must be forbidden (403)")
    class WriteOpsForbiddenForNonAdmin {

        @Test
        @DisplayName("POST /api/v1/drivingLicenseType returns 403 for CUSTOMER")
        @WithMockUser(roles = "CUSTOMER")
        void create_customerRole_returns403() throws Exception {
            mockMvc.perform(post("/api/v1/drivingLicenseType")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"B\"}"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("PUT /api/v1/drivingLicenseType returns 403 for CUSTOMER")
        @WithMockUser(roles = "CUSTOMER")
        void update_customerRole_returns403() throws Exception {
            mockMvc.perform(put("/api/v1/drivingLicenseType")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"id\":1,\"name\":\"B\"}"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("DELETE /api/v1/drivingLicenseType returns 403 for CUSTOMER")
        @WithMockUser(roles = "CUSTOMER")
        void delete_customerRole_returns403() throws Exception {
            mockMvc.perform(delete("/api/v1/drivingLicenseType")
                            .param("id", "1")
                            .param("isHardDelete", "false"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("POST /api/v1/drivingLicenseType returns 403 for EMPLOYEE")
        @WithMockUser(roles = "EMPLOYEE")
        void create_employeeRole_returns403() throws Exception {
            mockMvc.perform(post("/api/v1/drivingLicenseType")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"C\"}"))
                    .andExpect(status().isForbidden());
        }
    }

    // ======================================================================
    //  ADMIN role — full access to all operations
    // ======================================================================

    @Nested
    @DisplayName("ADMIN role – full access (read + write)")
    class AdminRoleFullAccess {

        @Test
        @DisplayName("GET /api/v1/drivingLicenseType succeeds for ADMIN")
        @WithMockUser(roles = "ADMIN")
        void getAll_adminRole_returns200() throws Exception {
            when(drivingLicenseTypeService.getAll()).thenReturn(List.of());
            mockMvc.perform(get("/api/v1/drivingLicenseType"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("POST /api/v1/drivingLicenseType returns 204 for ADMIN")
        @WithMockUser(roles = "ADMIN")
        void create_adminRole_returns204() throws Exception {
            mockMvc.perform(post("/api/v1/drivingLicenseType")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"B\",\"description\":\"Auto\"}"))
                    .andExpect(status().isNoContent());
        }
    }
}
