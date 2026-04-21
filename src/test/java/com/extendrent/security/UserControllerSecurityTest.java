package com.extendrent.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import src.controller.user.UserController;
import src.core.config.AppConfig;
import src.core.config.SecurityConfig;
import src.core.security.JwtService;
import src.service.user.UserService;

import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Security tests for {@link UserController}.
 *
 * Documents and enforces (PATCHED S1-1 / V01):
 * 1. All user-management endpoints require authentication.
 * 2. Listing / reading user data is restricted to ADMIN role.
 * 3. Blocking a user is restricted to ADMIN role.
 * 4. Password update (PUT /updatePassword) requires ADMIN role;
 *    unauthenticated callers get 401 and CUSTOMER gets 403.
 */
@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, AppConfig.class})
@ActiveProfiles("test")
@DisplayName("UserController – security tests")
class UserControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;

    // ======================================================================
    //  Access control – unauthenticated requests
    // ======================================================================

    @Nested
    @DisplayName("Unauthenticated access – MUST be rejected (401)")
    class UnauthenticatedAccess {

        @Test
        @DisplayName("GET /api/v1/users must return 401 without a token")
        void listAllUsers_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/users"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/users/{id} must return 401 without a token")
        void getUserById_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/users/1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/users (isDeleted filter) must return 401 without a token")
        void getUsersByDeletedState_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/users").param("isDeleted", "false"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/users/count/{isDeleted} must return 401 without a token")
        void getUserCount_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/users/count/false"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("PUT /api/v1/users/updatePassword must return 401 without a token")
        void updatePassword_noAuth_returns401() throws Exception {
            // Security patch V01: password in body; security check fires before body parsing.
            mockMvc.perform(put("/api/v1/users/updatePassword")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"id\":1,\"password\":\"newPassword1!\"}"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("PUT /api/v1/users/block/{id} must return 401 without a token")
        void blockUser_noAuth_returns401() throws Exception {
            mockMvc.perform(put("/api/v1/users/block/1"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================================================================
    //  Access control – authenticated as CUSTOMER (must be forbidden)
    // ======================================================================

    @Nested
    @DisplayName("CUSTOMER role must not access admin-only endpoints")
    class CustomerRoleAccessControl {

        @Test
        @DisplayName("GET /api/v1/users must return 403 for CUSTOMER role")
        @WithMockUser(roles = "CUSTOMER")
        void listAllUsers_customerRole_returns403() throws Exception {
            mockMvc.perform(get("/api/v1/users"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("PUT /api/v1/users/block/{id} must return 403 for CUSTOMER")
        @WithMockUser(roles = "CUSTOMER")
        void blockUser_customerRole_returns403() throws Exception {
            mockMvc.perform(put("/api/v1/users/block/1"))
                    .andExpect(status().isForbidden());
        }
    }

    // ======================================================================
    //  Password update – V01 patch + access control (ADMIN-only)
    // ======================================================================

    @Nested
    @DisplayName("V01 patch + RBAC – PUT /api/v1/users/updatePassword")
    class PasswordUpdateAccessControl {

        @Test
        @DisplayName("PUT with old query-param style returns 401 (unauthenticated — security check fires first)")
        void updatePassword_noAuth_queryParams_returns401() throws Exception {
            // Security check (401) fires before body parsing, so old-style calls still get 401.
            mockMvc.perform(put("/api/v1/users/updatePassword")
                            .param("id", "1")
                            .param("password", "attackerNewPassword"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("PUT with JSON body and no auth returns 401 (ADMIN role required)")
        void updatePassword_jsonBody_noAuth_returns401() throws Exception {
            // Security patch V01: body is now the accepted format.
            // Unauthenticated callers get 401 regardless.
            mockMvc.perform(put("/api/v1/users/updatePassword")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"id\":1,\"password\":\"newSecure!99\"}"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("CUSTOMER role is forbidden (endpoint requires ADMIN)")
        @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
        void updatePassword_customerRole_returns403() throws Exception {
            mockMvc.perform(put("/api/v1/users/updatePassword")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"id\":1,\"password\":\"newSecure!99\"}"))
                    .andExpect(status().isForbidden());
        }
    }

    // ======================================================================
    //  ADMIN role has correct access
    // ======================================================================

    @Nested
    @DisplayName("ADMIN role – correct access (should pass after security is fixed)")
    class AdminRoleAccess {

        @Test
        @DisplayName("GET /api/v1/users returns data for ADMIN role")
        @WithMockUser(roles = "ADMIN")
        void listAllUsers_adminRole_succeeds() throws Exception {
            when(userService.getAll(any())).thenReturn(Page.empty());
            mockMvc.perform(get("/api/v1/users"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET /api/v1/users/{id} returns data for ADMIN role")
        @WithMockUser(roles = "ADMIN")
        void getUserById_adminRole_succeeds() throws Exception {
            when(userService.getById(1)).thenReturn(null);
            mockMvc.perform(get("/api/v1/users/1"))
                    .andExpect(status().isOk());
        }
    }

    // ======================================================================
    //  Input sanity on path variables
    // ======================================================================

    @Nested
    @DisplayName("Path variable type safety")
    class PathVariableTypeSafety {

        @Test
        @DisplayName("GET /api/v1/users/notAnId returns 400 (non-numeric id)")
        @WithMockUser(roles = "ADMIN")
        void getById_nonNumericId_returns400() throws Exception {
            mockMvc.perform(get("/api/v1/users/notAnId"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("PUT /api/v1/users/block/notAnId returns 400 (non-numeric id)")
        @WithMockUser(roles = "ADMIN")
        void blockUser_nonNumericId_returns400() throws Exception {
            mockMvc.perform(put("/api/v1/users/block/notAnId"))
                    .andExpect(status().isBadRequest());
        }
    }
}
