package com.lehdonjoki.listoo.controller;

import com.lehdonjoki.listoo.model.User;
import com.lehdonjoki.listoo.service.AuthService;
import jakarta.annotation.security.PermitAll;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;
  private final AuthenticationManager authenticationManager;

  public AuthController(AuthService authService, AuthenticationManager authenticationManager) {
    this.authService = authService;
    this.authenticationManager = authenticationManager;
  }

  /** ✅ Endpoint to register a new user. */
  @PostMapping("/register")
  @PermitAll
  public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
    String email = request.get("email");
    String password = request.get("password");
    String name = request.get("name");

    try {
      User newUser = authService.register(email, password, name);
      return ResponseEntity.ok(
          Map.of("message", "User registered successfully.", "userId", newUser.getId()));
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  /** ✅ Endpoint to authenticate a user and return access and refresh tokens. */
  @PostMapping("/login")
  @PermitAll
  public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
    String email = request.get("email");
    String password = request.get("password");

    try {
      // Authenticate user using email as the username
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(email, password));

      // Generate tokens after successful authentication
      Map<String, String> tokens = authService.generateTokens(authentication);
      return ResponseEntity.ok(tokens);
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  /** ✅ Endpoint to refresh access and refresh tokens. */
  @PostMapping("/refresh-token")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> refreshAccessToken(@RequestBody Map<String, String> request) {
    String refreshToken = request.get("refreshToken");

    if (refreshToken == null || refreshToken.isEmpty()) {
      return ResponseEntity.badRequest().body(Map.of("error", "Refresh token is required."));
    }

    try {
      Map<String, String> tokens = authService.refreshToken(refreshToken);
      return ResponseEntity.ok(tokens);
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  /** ✅ Endpoint to log out a user by deleting their refresh tokens. */
  @PostMapping("/logout")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> logout(@RequestBody Map<String, String> request) {
    String email = request.get("email");

    try {
      authService.logout(email);
      return ResponseEntity.ok(Map.of("message", "Logged out successfully."));
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }
}
