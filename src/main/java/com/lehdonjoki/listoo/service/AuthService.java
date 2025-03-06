package com.lehdonjoki.listoo.service;

import com.lehdonjoki.listoo.model.User;
import com.lehdonjoki.listoo.repository.UserRepository;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public User register(String email, String password, String name) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email is already in use.");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);

        return userRepository.save(user);
    }

    public Map<String, String> generateTokens(Authentication authentication) {
        String email = authentication.getName();
        return tokenService.generateTokens(email);
    }

    public Map<String, String> refreshToken(String refreshToken) {
        return tokenService.refreshToken(refreshToken);
    }

    public void logout(String email) {
        // No DB operation needed since refresh tokens are JWTs
    }

    public User getUserByEmail(String email) {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }
}
