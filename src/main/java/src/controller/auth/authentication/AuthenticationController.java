package src.controller.auth.authentication;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import src.controller.TResponse;
import src.controller.auth.authentication.request.IsUserTrueRequest;
import src.controller.auth.authentication.request.SignInRequest;
import src.controller.auth.authentication.request.SignUpReqeust;
import src.core.security.model.JwtToken;
import src.service.auth.AuthenticationService;
import src.service.external.EmailService;

import java.time.Duration;

import static src.controller.auth.authentication.LogConstant.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final EmailService emailService;
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    ResponseEntity<Void> signUp(@Valid @RequestBody SignUpReqeust request) {
        log.info(USER_SIGN_UP_REQUEST_RECEIVED, request.getEmailAddress());
        authenticationService.signUp(request);
        log.info(USER_SIGN_UP_SUCCESSFUL, request.getEmailAddress());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // V-02: access token delivered via HttpOnly cookie — not readable by JavaScript
    @PostMapping("/signin")
    ResponseEntity<TResponse<JwtToken>> signIn(@Valid @RequestBody SignInRequest request,
                                               HttpServletResponse httpResponse) {
        log.info(USER_SIGN_IN_REQUEST_RECEIVED, request.getEmail());
        JwtToken tokens = authenticationService.signIn(request);

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", tokens.getToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .maxAge(Duration.ofHours(24))
                .path("/")
                .build();
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());

        // Refresh token also HttpOnly — only readable by /refresh-token endpoint
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokens.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .maxAge(Duration.ofDays(7))
                .path("/api/v1/refresh-token")
                .build();
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        log.info(USER_SIGN_IN_SUCCESSFUL, request.getEmail());
        // Return body without tokens — cookie is the authoritative delivery mechanism
        TResponse<JwtToken> response = TResponse.<JwtToken>tResponseBuilder()
                .response(JwtToken.builder().build())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // V-04: logout revokes all refresh tokens; clears cookies
    @PostMapping("/logout")
    ResponseEntity<Void> logout(@AuthenticationPrincipal UserDetails userDetails,
                                HttpServletResponse httpResponse) {
        if (userDetails != null) {
            authenticationService.logout(userDetails.getUsername());
        }
        // Expire both cookies immediately
        ResponseCookie clearAccess = ResponseCookie.from("accessToken", "")
                .httpOnly(true).secure(true).sameSite("Strict").maxAge(0).path("/").build();
        ResponseCookie clearRefresh = ResponseCookie.from("refreshToken", "")
                .httpOnly(true).secure(true).sameSite("Strict").maxAge(0).path("/api/v1/refresh-token").build();
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, clearAccess.toString());
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, clearRefresh.toString());
        return ResponseEntity.noContent().build();
    }

    // Security patch V01: converted from GET+query-params to POST+body so the
    // password is never written to server logs, browser history, or Referer headers.
    @PostMapping("/isUserTrue")
    public ResponseEntity<TResponse<Boolean>> isCustomerTrue(
            @Valid @RequestBody IsUserTrueRequest request) {
        log.info(CHECKING_USER_CREDENTIALS, request.getEmail());
        TResponse<Boolean> response = TResponse.<Boolean>tResponseBuilder()
                .response(authenticationService.isUserTrue(request.getEmail(), request.getPassword()))
                .build();
        log.info(USER_CREDENTIALS_CHECKED, request.getEmail());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
