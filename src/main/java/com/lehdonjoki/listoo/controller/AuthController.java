package com.lehdonjoki.listoo.controller;

import com.lehdonjoki.listoo.model.RefreshToken;
import com.lehdonjoki.listoo.model.User;
import com.lehdonjoki.listoo.repository.UserRepository;
import com.lehdonjoki.listoo.security.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public AuthController(JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAccessToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Refresh token is required."));
        }

        try {
            Map<String, String> tokens = jwtTokenProvider.rotateRefreshToken(refreshToken);
            return ResponseEntity.ok(tokens);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));

        // Password check (should be using PasswordEncoder in production)
        if (!user.getPassword().equals(password)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials."));
        }

        String accessToken = jwtTokenProvider.generateAccessToken(email);
        RefreshToken refreshToken = jwtTokenProvider.generateRefreshToken(user);

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken.getToken()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));

        jwtTokenProvider.logout(user);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully."));
    }
}
