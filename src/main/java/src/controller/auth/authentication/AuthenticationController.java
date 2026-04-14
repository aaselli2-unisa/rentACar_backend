package src.controller.auth.authentication;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import src.controller.TResponse;
import src.controller.auth.authentication.request.IsUserTrueRequest;
import src.controller.auth.authentication.request.SignInRequest;
import src.controller.auth.authentication.request.SignUpReqeust;
import src.core.security.model.JwtToken;
import src.service.auth.AuthenticationService;
import src.service.external.EmailService;

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

    @PostMapping("/signin")
    ResponseEntity<TResponse<JwtToken>> signIn(@Valid @RequestBody SignInRequest request) {
        log.info(USER_SIGN_IN_REQUEST_RECEIVED, request.getEmail());
        TResponse<JwtToken> response = TResponse.<JwtToken>tResponseBuilder()
                .response(authenticationService.signIn(request)).build();
        log.info(USER_SIGN_IN_SUCCESSFUL, request.getEmail());
        return new ResponseEntity<>(response, HttpStatus.OK);
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
