
package com.fraudwatch.fraudruleengine.controller;

import com.fraudwatch.fraudruleengine.dto.AuthResponse;
import com.fraudwatch.fraudruleengine.dto.LoginRequest;
import com.fraudwatch.fraudruleengine.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @Operation(summary = "Login and get JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authenticationService.login(loginRequest);
        return ResponseEntity.ok(authResponse);
    }
}
