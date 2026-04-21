package com.extendrent.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import src.controller.user.UserController;
import src.controller.user.admin.AdminController;
import src.controller.user.employee.EmployeeController;
import src.controller.rental.RentalController;
import src.controller.auth.authentication.AuthenticationController;
import src.controller.discount.DiscountController;
import src.core.config.AppConfig;
import src.core.config.SecurityConfig;
import src.core.security.JwtService;
import src.service.auth.AuthenticationService;
import src.service.discount.DiscountService;
import src.service.external.EmailService;
import src.service.rental.RentalService;
import src.service.rental.status.RentalStatusService;
import src.service.user.UserService;
import src.service.user.admin.AdminService;
import src.service.user.employee.EmployeeService;

import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security filter-chain integration tests (PATCHED S1-1).
 *
 * Each test verifies the required access-control posture. All sensitive endpoints
 * now require authentication or the appropriate role. These tests are regression
 * guards — they turn red if SecurityConfig is inadvertently relaxed.
 */
@WebMvcTest({
        UserController.class,
        AdminController.class,
        EmployeeController.class,
        RentalController.class,
        AuthenticationController.class,
        DiscountController.class
})
@Import({SecurityConfig.class, AppConfig.class})
@ActiveProfiles("test")
@DisplayName("Security filter-chain – access control matrix")
class SecurityFilterChainTest {

    @Autowired
    private MockMvc mockMvc;

    /* Security-infrastructure mocks (satisfy JwtAuthFilter and SecurityConfig deps) */
    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;

    /* Controller-dependency mocks */
    @MockBean private AdminService adminService;
    @MockBean private EmployeeService employeeService;
    @MockBean private RentalService rentalService;
    @MockBean private RentalStatusService rentalStatusService;
    @MockBean private AuthenticationService authenticationService;
    @MockBean private EmailService emailService;
    @MockBean private DiscountService discountService;

    // ======================================================================
    //  Auth endpoints — must remain publicly accessible
    // ======================================================================

    @Nested
    @DisplayName("Auth endpoints (must be publicly accessible)")
    class AuthEndpoints {

        @Test
        @DisplayName("POST /api/v1/auth/signup is accessible without a token")
        void signup_isPublic() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name":"Test","surname":"User","emailAddress":"t@t.com",
                                    "password":"12345678","phoneNumber":"5551234567",
                                    "authority":"CUSTOMER","userImageEntityId":1}"""))
                    .andExpect(status().is(not(401)));
        }

        @Test
        @DisplayName("POST /api/v1/auth/signin is accessible without a token")
        void signin_isPublic() throws Exception {
            mockMvc.perform(post("/api/v1/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"email":"user@example.com","password":"password"}"""))
                    .andExpect(status().is(not(401)));
        }

        @Test
        @DisplayName("PATCHED V01: GET /api/v1/auth/isUserTrue no longer accepts query-param credentials")
        void isUserTrue_getWithQueryParams_isBlocked() throws Exception {
            // V01 fix: endpoint changed to POST with JSON body.
            // GET is no longer a valid method — Spring Security returns 401 before dispatch,
            // or the dispatcher returns 405. Both confirm credentials cannot be passed via URL.
            int status = mockMvc.perform(get("/api/v1/auth/isUserTrue")
                            .param("email", "admin@example.com")
                            .param("password", "secret"))
                    .andReturn().getResponse().getStatus();
            org.assertj.core.api.Assertions.assertThat(status)
                    .as("GET /api/v1/auth/isUserTrue must return 401 or 405, not 200 or 500. Got: %d", status)
                    .isIn(401, 405);
        }
    }

    // ======================================================================
    //  /api/v1/users — requires authentication (PATCHED S1-1)
    // ======================================================================

    @Nested
    @DisplayName("/api/v1/users – all endpoints require authentication (PATCHED S1-1)")
    class UserEndpointProtection {

        @Test
        @DisplayName("GET /api/v1/users must require authentication")
        void listAllUsers_mustRequireAuth() throws Exception {
            mockMvc.perform(get("/api/v1/users"))
                    .andExpect(status().isUnauthorized()); // 401 — PATCHED S1-1 (was 200 before fix)
        }

        @Test
        @DisplayName("GET /api/v1/users/{id} must require authentication")
        void getUserById_mustRequireAuth() throws Exception {
            mockMvc.perform(get("/api/v1/users/1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("PUT /api/v1/users/updatePassword must require authentication")
        void updatePassword_mustRequireAuth() throws Exception {
            mockMvc.perform(put("/api/v1/users/updatePassword")
                            .param("id", "1")
                            .param("password", "newpassword"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("PUT /api/v1/users/block/{id} must require ADMIN role")
        void blockUser_mustRequireAdminRole() throws Exception {
            mockMvc.perform(put("/api/v1/users/block/1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/users (deleted filter) must require authentication")
        void listUsersByDeletedState_mustRequireAuth() throws Exception {
            mockMvc.perform(get("/api/v1/users").param("isDeleted", "false"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================================================================
    //  /api/v1/admins — requires ADMIN role (PATCHED S1-1)
    // ======================================================================

    @Nested
    @DisplayName("/api/v1/admins – all endpoints require authentication (PATCHED S1-1)")
    class AdminEndpointProtection {

        @Test
        @DisplayName("POST /api/v1/admins must require ADMIN role")
        void createAdmin_mustRequireAdminRole() throws Exception {
            mockMvc.perform(post("/api/v1/admins")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/admins must require authentication")
        void listAdmins_mustRequireAuth() throws Exception {
            mockMvc.perform(get("/api/v1/admins"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("DELETE /api/v1/admins must require ADMIN role")
        void deleteAdmin_mustRequireAdminRole() throws Exception {
            mockMvc.perform(delete("/api/v1/admins")
                            .param("id", "1")
                            .param("isHardDelete", "true"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================================================================
    //  /api/v1/employees — requires admin/employee role (PATCHED S1-1)
    // ======================================================================

    @Nested
    @DisplayName("/api/v1/employees – all endpoints require authentication (PATCHED S1-1)")
    class EmployeeEndpointProtection {

        @Test
        @DisplayName("POST /api/v1/employees must require ADMIN role")
        void createEmployee_mustRequireAdminRole() throws Exception {
            mockMvc.perform(post("/api/v1/employees")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/employees must require authentication")
        void listEmployees_mustRequireAuth() throws Exception {
            mockMvc.perform(get("/api/v1/employees"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("DELETE /api/v1/employees must require ADMIN role")
        void deleteEmployee_mustRequireAdminRole() throws Exception {
            mockMvc.perform(delete("/api/v1/employees")
                            .param("id", "1")
                            .param("isHardDelete", "false"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/employees (salary range) requires authentication – salary PII protected (PATCHED S1-1)")
        void salaryFilter_mustRequireAuth() throws Exception {
            // Salary data is sensitive PII and must not be accessible without authentication
            mockMvc.perform(get("/api/v1/employees")
                            .param("startSalary", "0")
                            .param("endSalary", "100000"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================================================================
    //  /api/v1/rentals — requires authentication (PATCHED S1-1)
    // ======================================================================

    @Nested
    @DisplayName("/api/v1/rentals – all endpoints require authentication (PATCHED S1-1)")
    class RentalEndpointProtection {

        @Test
        @DisplayName("GET /api/v1/rentals must require authentication")
        void listAllRentals_mustRequireAuth() throws Exception {
            mockMvc.perform(get("/api/v1/rentals"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("POST /api/v1/rentals must require authentication")
        void createRental_mustRequireAuth() throws Exception {
            mockMvc.perform(post("/api/v1/rentals")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("DELETE /api/v1/rentals must require ADMIN role")
        void deleteRental_mustRequireAdminRole() throws Exception {
            mockMvc.perform(delete("/api/v1/rentals")
                            .param("id", "1")
                            .param("isHardDelete", "true"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("PUT /api/v1/rentals/startRental/{id} must require authentication")
        void startRental_mustRequireAuth() throws Exception {
            mockMvc.perform(put("/api/v1/rentals/startRental/1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("PUT /api/v1/rentals/cancelRental/{id} must require authentication")
        void cancelRental_mustRequireAuth() throws Exception {
            mockMvc.perform(put("/api/v1/rentals/cancelRental/1"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================================================================
    //  /api/v1/discounts — write operations must require ADMIN role
    // ======================================================================

    @Nested
    @DisplayName("/api/v1/discounts – write operations require authentication (PATCHED S1-1)")
    class DiscountEndpointProtection {

        @Test
        @DisplayName("POST /api/v1/discounts must require ADMIN role")
        void createDiscount_mustRequireAdminRole() throws Exception {
            mockMvc.perform(post("/api/v1/discounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("DELETE /api/v1/discounts must require ADMIN role")
        void deleteDiscount_mustRequireAdminRole() throws Exception {
            mockMvc.perform(delete("/api/v1/discounts")
                            .param("id", "1")
                            .param("isHardDelete", "false"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================================================================
    //  Completely unknown / non-existent paths
    // ======================================================================

    @Nested
    @DisplayName("Unknown paths")
    class UnknownPaths {

        @ParameterizedTest(name = "{0}")
        @ValueSource(strings = {
                "/api/v1/unknown-resource",
                "/api/v1/../etc/passwd",
                "/api/v1/admin", // no trailing slash
        })
        @DisplayName("Unknown paths must return 4xx, not 200")
        void unknownPaths_return4xx(String path) throws Exception {
            mockMvc.perform(get(path))
                    .andExpect(status().is4xxClientError());
        }
    }

    // ======================================================================
    //  Request without Authorization header on secured paths
    // ======================================================================

    @Test
    @DisplayName("Request with an empty Authorization header must not authenticate")
    void emptyAuthorizationHeader_doesNotAuthenticate() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", ""))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Request with malformed Bearer value must not authenticate")
    void malformedBearer_doesNotAuthenticate() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer not-a-jwt"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Request with non-Bearer scheme must not authenticate")
    void basicAuthScheme_doesNotAuthenticate() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Basic dXNlcjpwYXNz"))
                .andExpect(status().isUnauthorized());
    }
}
