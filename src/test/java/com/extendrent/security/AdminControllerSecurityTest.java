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
import src.controller.user.admin.AdminController;
import src.core.config.AppConfig;
import src.core.config.SecurityConfig;
import src.core.security.JwtService;
import src.service.user.UserService;
import src.service.user.admin.AdminService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security tests for {@link AdminController}.
 *
 * Admin management is one of the most sensitive areas of the application.
 * Every write endpoint must be restricted to the ADMIN role.
 * Every read endpoint must require authentication at minimum.
 *
 * All endpoints now require ADMIN role (PATCHED S1-1). These tests are
 * regression guards that turn red if access control is inadvertently relaxed.
 */
@WebMvcTest(AdminController.class)
@Import({SecurityConfig.class, AppConfig.class})
@ActiveProfiles("test")
@DisplayName("AdminController – security tests")
class AdminControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;
    @MockBean private AdminService adminService;

    // ======================================================================
    //  Unauthenticated access – must be rejected
    // ======================================================================

    @Nested
    @DisplayName("Unauthenticated access – MUST be rejected (401)")
    class UnauthenticatedAccess {

        @Test
        @DisplayName("GET /api/v1/admins must return 401 without a token")
        void listAdmins_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/admins"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/admins/{id} must return 401 without a token")
        void getAdminById_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/admins/1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("POST /api/v1/admins must return 401 without a token")
        void createAdmin_noAuth_returns401() throws Exception {
            mockMvc.perform(post("/api/v1/admins")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validAdminJson()))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("PUT /api/v1/admins must return 401 without a token")
        void updateAdmin_noAuth_returns401() throws Exception {
            mockMvc.perform(put("/api/v1/admins")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("DELETE /api/v1/admins must return 401 without a token")
        void deleteAdmin_noAuth_returns401() throws Exception {
            mockMvc.perform(delete("/api/v1/admins")
                            .param("id", "1")
                            .param("isHardDelete", "true"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/admins/count/{isDeleted} must return 401 without a token")
        void adminCount_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/admins/count/false"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/admins (isDeleted filter) must return 401 without a token")
        void adminsByDeletedState_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/admins").param("isDeleted", "false"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================================================================
    //  Non-ADMIN authenticated access – must be forbidden
    // ======================================================================

    @Nested
    @DisplayName("Non-ADMIN role – MUST be forbidden (403) on write endpoints")
    class NonAdminRoleAccess {

        @Test
        @DisplayName("POST /api/v1/admins must return 403 for CUSTOMER")
        @WithMockUser(roles = "CUSTOMER")
        void createAdmin_customerRole_returns403() throws Exception {
            mockMvc.perform(post("/api/v1/admins")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validAdminJson()))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("DELETE /api/v1/admins must return 403 for EMPLOYEE")
        @WithMockUser(roles = "EMPLOYEE")
        void deleteAdmin_employeeRole_returns403() throws Exception {
            mockMvc.perform(delete("/api/v1/admins")
                            .param("id", "1")
                            .param("isHardDelete", "false"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("PUT /api/v1/admins must return 403 for CUSTOMER")
        @WithMockUser(roles = "CUSTOMER")
        void updateAdmin_customerRole_returns403() throws Exception {
            mockMvc.perform(put("/api/v1/admins")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isForbidden());
        }
    }

    // ======================================================================
    //  ADMIN role – happy path (should succeed after security is fixed)
    // ======================================================================

    @Nested
    @DisplayName("ADMIN role – should have full access")
    class AdminRoleAccess {

        @Test
        @DisplayName("GET /api/v1/admins succeeds for ADMIN role")
        @WithMockUser(roles = "ADMIN")
        void listAdmins_adminRole_succeeds() throws Exception {
            when(adminService.getAll()).thenReturn(List.of());
            mockMvc.perform(get("/api/v1/admins"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET /api/v1/admins/count/{isDeleted} succeeds for ADMIN role")
        @WithMockUser(roles = "ADMIN")
        void adminCount_adminRole_succeeds() throws Exception {
            when(adminService.getCountByDeletedState(false)).thenReturn(5);
            mockMvc.perform(get("/api/v1/admins/count/false"))
                    .andExpect(status().isOk());
        }
    }

    // ======================================================================
    //  Hard-delete endpoint — extra-sensitive, must require ADMIN
    // ======================================================================

    @Nested
    @DisplayName("Hard-delete endpoint – special scrutiny")
    class HardDeleteEndpoint {

        @Test
        @DisplayName("Hard delete without auth must return 401")
        void hardDelete_noAuth_returns401() throws Exception {
            mockMvc.perform(delete("/api/v1/admins")
                            .param("id", "1")
                            .param("isHardDelete", "true"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Hard delete as CUSTOMER must return 403")
        @WithMockUser(roles = "CUSTOMER")
        void hardDelete_customerRole_returns403() throws Exception {
            mockMvc.perform(delete("/api/v1/admins")
                            .param("id", "1")
                            .param("isHardDelete", "true"))
                    .andExpect(status().isForbidden());
        }
    }

    // ---- helpers -----------------------------------------------------------

    private static String validAdminJson() {
        return """
                {"name":"Admin","surname":"User","emailAddress":"admin@example.com",
                "password":"12345678","phoneNumber":"5551234567",
                "salary":50000.0,"userImageEntityId":1}""";
    }
}
