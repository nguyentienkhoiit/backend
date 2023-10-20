package com.capstone.backend.controller;

import static com.capstone.backend.utils.Constants.API_VERSION;

import com.capstone.backend.model.dto.user.UserEmailDTORequest;
import com.capstone.backend.model.dto.user.UserForgotPasswordDTORequest;
import com.capstone.backend.service.ConfirmationTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import com.capstone.backend.model.dto.authentication.AuthenticationDTORequest;
import com.capstone.backend.service.AuthenticationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(API_VERSION + "/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Authentication", description = "API for Authentication")
public class AuthenticationController {
    AuthenticationService authenticationService;
    ConfirmationTokenService confirmationTokenService;

    @PostMapping("/login")
    @Operation(summary = "Login to basic authentication")
    public ResponseEntity<?> login(@Valid @RequestBody AuthenticationDTORequest request) throws Exception {
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @GetMapping("/oauth2")
    @Operation(summary = "Login to google authentication")
    public Map<String, Object> login(OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        return oAuth2AuthenticationToken.getPrincipal().getAttributes();
    }

    @PostMapping("forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody UserEmailDTORequest request) {
        return ResponseEntity.ok(authenticationService.forgotPassword(request));
    }

    @GetMapping("/confirm")
    @Operation(summary = "Confirm token email for forgot password")
    public String confirm(@RequestParam(value = "token", required = true) String token) {
        return confirmationTokenService.goToForgotPassword(token);
    }

    @PostMapping("submit-forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody UserForgotPasswordDTORequest request) {
        return ResponseEntity.ok(authenticationService.changePassword(request));
    }
}
