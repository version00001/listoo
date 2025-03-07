package com.lehdonjoki.listoo.controller;

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
public class AuthControllerTest {

  private final AuthService authService;
  private final AuthenticationManager authenticationManager;

  public AuthControllerTest(AuthService authService, AuthenticationManager authenticationManager) {
    this.authService = authService;
    this.authenticationManager = authenticationManager;
  }

  @PostMapping("/register")
  @PermitAll
  public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
    String email = request.get("email");
    String password = request.get("password");
    String name = request.get("name");

    try {
      authService.register(email, password, name);
      return ResponseEntity.ok(Map.of("message", "User registered successfully."));
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  @PostMapping("/login")
  @PermitAll
  public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
    String email = request.get("email");
    String password = request.get("password");

    try {
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(email, password));

      Map<String, String> tokens = authService.generateTokens(authentication);
      return ResponseEntity.ok(tokens);
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

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

  @PostMapping("/logout")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> logout(@RequestBody Map<String, String> request) {
    return ResponseEntity.ok(Map.of("message", "Logged out successfully."));
  }
}
