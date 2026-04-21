package com.extendrent.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import src.controller.auth.authentication.AuthenticationController;
import src.core.config.AppConfig;
import src.core.config.SecurityConfig;
import src.core.security.JwtService;
import src.core.security.model.JwtToken;
import src.service.auth.AuthenticationService;
import src.service.external.EmailService;
import src.service.user.UserService;

import jakarta.servlet.http.Cookie;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * V-02 – Access token delivered via HttpOnly cookie (OWASP A07 / CWE-614).
 *
 * Tokens in localStorage are readable by any JavaScript running on the page
 * (XSS, malicious extensions). HttpOnly cookies cannot be accessed via
 * document.cookie and are automatically stripped by the browser on non-matching origins.
 *
 * Fix: signin response sets Set-Cookie: accessToken=...; HttpOnly; Secure; SameSite=Strict
 */
@WebMvcTest(AuthenticationController.class)
@Import({SecurityConfig.class, AppConfig.class})
@ActiveProfiles("test")
@DisplayName("V-02 – Token delivered via HttpOnly cookie, not response body")
class HttpOnlyCookieSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;
    @MockBean private AuthenticationService authenticationService;
    @MockBean private EmailService emailService;

    @Test
    @DisplayName("POST /signin sets accessToken cookie with HttpOnly flag")
    void signin_setsHttpOnlyCookie() throws Exception {
        when(authenticationService.signIn(any())).thenReturn(
                JwtToken.builder().token("access.token.here").refreshToken("refresh.token.here").build());

        MvcResult result = mockMvc.perform(post("/api/v1/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@example.com\",\"password\":\"Str0ng@Pass\"}"))
                .andExpect(status().isOk())
                .andReturn();

        Cookie accessCookie = result.getResponse().getCookie("accessToken");
        assertThat(accessCookie)
                .as("accessToken cookie must be present in signin response")
                .isNotNull();
        assertThat(accessCookie.isHttpOnly())
                .as("accessToken cookie must be HttpOnly — JavaScript must not read it")
                .isTrue();
        assertThat(accessCookie.getSecure())
                .as("accessToken cookie must have Secure flag — only sent over HTTPS")
                .isTrue();
    }

    @Test
    @DisplayName("POST /signin sets refreshToken cookie with HttpOnly flag")
    void signin_setsHttpOnlyRefreshCookie() throws Exception {
        when(authenticationService.signIn(any())).thenReturn(
                JwtToken.builder().token("access.token.here").refreshToken("refresh.token.here").build());

        MvcResult result = mockMvc.perform(post("/api/v1/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@example.com\",\"password\":\"Str0ng@Pass\"}"))
                .andExpect(status().isOk())
                .andReturn();

        Cookie refreshCookie = result.getResponse().getCookie("refreshToken");
        assertThat(refreshCookie)
                .as("refreshToken cookie must be present in signin response")
                .isNotNull();
        assertThat(refreshCookie.isHttpOnly())
                .as("refreshToken cookie must be HttpOnly")
                .isTrue();
    }

    @Test
    @DisplayName("POST /signin response body does NOT contain the raw token value")
    void signin_responseBody_doesNotContainRawToken() throws Exception {
        String rawToken = "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwicm9sZSI6IkNVU1RPTUVSIn0.signature";
        when(authenticationService.signIn(any())).thenReturn(
                JwtToken.builder().token(rawToken).refreshToken("refresh.token.here").build());

        MvcResult result = mockMvc.perform(post("/api/v1/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@example.com\",\"password\":\"Str0ng@Pass\"}"))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThat(body)
                .as("Raw access token must not appear in response body — delivered via cookie only")
                .doesNotContain(rawToken);
    }

    @Test
    @DisplayName("POST /logout clears accessToken cookie (maxAge=0)")
    void logout_clearsCookies() throws Exception {
        String token = SecurityTestSupport.validJwt("user@example.com", src.service.user.model.UserRole.CUSTOMER);
        SecurityTestSupport.setupAuthMocks(jwtService, userService, token,
                SecurityTestSupport.userEntity("user@example.com", src.service.user.model.UserRole.CUSTOMER));

        MvcResult result = mockMvc.perform(post("/api/v1/auth/logout")
                        .cookie(new Cookie("accessToken", token)))
                .andExpect(status().isNoContent())
                .andReturn();

        Cookie cleared = result.getResponse().getCookie("accessToken");
        assertThat(cleared)
                .as("Logout must set an expired accessToken cookie to clear it from the browser")
                .isNotNull();
        assertThat(cleared.getMaxAge())
                .as("Cleared cookie must have maxAge=0")
                .isEqualTo(0);
    }
}
