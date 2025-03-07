package com.lehdonjoki.listoo.controller;

import com.lehdonjoki.listoo.dto.AuthRequestDTO;
import com.lehdonjoki.listoo.dto.RegisterRequestDTO;
import com.lehdonjoki.listoo.dto.RefreshTokenRequestDTO;
import com.lehdonjoki.listoo.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }

    @Operation(summary = "Register a new user", description = "Registers a user with email, password, and name.")
    @PostMapping("/register")
    @PermitAll
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO request) {
        try {
            authService.register(request.getEmail(), request.getPassword(), request.getName());
            return ResponseEntity.ok(Map.of("message", "User registered successfully."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "User login", description = "Authenticates a user and returns access and refresh tokens.")
    @PostMapping("/login")
    @PermitAll
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDTO request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            Map<String, String> tokens = authService.generateTokens(authentication);
            return ResponseEntity.ok(tokens);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Refresh access token", description = "Uses a refresh token to get a new access token.")
    @PostMapping("/refresh-token")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> refreshAccessToken(@Valid @RequestBody RefreshTokenRequestDTO request) {
        try {
            Map<String, String> tokens = authService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(tokens);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Logout user", description = "Logs out the user by deleting their refresh tokens.")
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "Logged out successfully."));
    }
}
