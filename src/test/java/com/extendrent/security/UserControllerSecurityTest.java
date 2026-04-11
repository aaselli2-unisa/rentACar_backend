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
 * Documents and enforces:
 * 1. All user-management endpoints MUST require authentication.
 * 2. Listing / reading user data MUST be restricted to ADMIN role.
 * 3. Blocking a user MUST be restricted to ADMIN role.
 * 4. IDOR / password-update vulnerability — any caller can change any user's
 *    password with no identity check and no current-password verification.
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
        @DisplayName("GET /api/v1/users must return 401 without a token – FAILS until fixed")
        void listAllUsers_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/users"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/users/{id} must return 401 without a token – FAILS until fixed")
        void getUserById_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/users/1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/users (isDeleted filter) must return 401 without a token – FAILS until fixed")
        void getUsersByDeletedState_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/users").param("isDeleted", "false"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/users/count/{isDeleted} must return 401 without a token – FAILS until fixed")
        void getUserCount_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/v1/users/count/false"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("PUT /api/v1/users/updatePassword must return 401 without a token – FAILS until fixed")
        void updatePassword_noAuth_returns401() throws Exception {
            mockMvc.perform(put("/api/v1/users/updatePassword")
                            .param("id", "1")
                            .param("password", "newpassword"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("PUT /api/v1/users/block/{id} must return 401 without a token – FAILS until fixed")
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
        @DisplayName("GET /api/v1/users must return 403 for CUSTOMER role – FAILS until fixed")
        @WithMockUser(roles = "CUSTOMER")
        void listAllUsers_customerRole_returns403() throws Exception {
            mockMvc.perform(get("/api/v1/users"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("PUT /api/v1/users/block/{id} must return 403 for CUSTOMER – FAILS until fixed")
        @WithMockUser(roles = "CUSTOMER")
        void blockUser_customerRole_returns403() throws Exception {
            mockMvc.perform(put("/api/v1/users/block/1"))
                    .andExpect(status().isForbidden());
        }
    }

    // ======================================================================
    //  IDOR – Password update vulnerability
    // ======================================================================

    @Nested
    @DisplayName("VULNERABILITY – IDOR: password update with no ownership or identity check")
    class PasswordUpdateIdorVulnerability {

        @Test
        @DisplayName("PUT /api/v1/users/updatePassword accepts arbitrary user ID without auth – FAILS until fixed")
        void updatePassword_arbitraryId_noAuth_succeeds() throws Exception {
            // Any unauthenticated caller can change any user's password by specifying their numeric ID.
            // The endpoint is publicly accessible AND performs no ownership verification.
            // Expected CORRECT behaviour: 401 (unauthenticated) → should also verify current password
            mockMvc.perform(put("/api/v1/users/updatePassword")
                            .param("id", "1")       // victim's ID
                            .param("password", "attackerNewPassword"))
                    .andExpect(status().isUnauthorized()); // currently returns 204 – vulnerability!
        }

        @Test
        @DisplayName("Password passed as a query parameter is visible in server logs")
        void updatePassword_passwordInQueryParam_isLogged() throws Exception {
            // The password is transmitted as a URL query parameter, which will be recorded in:
            // - Web-server access logs
            // - Load-balancer request logs
            // - Browser history / bookmarks
            // The endpoint MUST accept the new password in the request body, not in the query string.
            // This test documents the design flaw — no assertion needed, the test name is the doc.
            mockMvc.perform(put("/api/v1/users/updatePassword")
                            .param("id", "99")
                            .param("password", "SuperSecret!"))
                    // Currently the request is accepted (200/204) — not rejected for bad design.
                    .andExpect(status().is(not(405))); // method still supported (not removed)
        }

        @Test
        @DisplayName("CUSTOMER authenticated user can update ANY other user's password – FAILS until fixed")
        @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
        void updatePassword_customerCanUpdateAnyUser() throws Exception {
            // Even after authentication is enforced, the lack of ownership check means
            // one customer can overwrite another customer's (or an admin's) password.
            // Required fix: @PreAuthorize("#id == principal.id")
            mockMvc.perform(put("/api/v1/users/updatePassword")
                            .param("id", "999")  // different user's ID
                            .param("password", "hackedPassword"))
                    .andExpect(status().isForbidden()); // must be 403 — currently 204
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
