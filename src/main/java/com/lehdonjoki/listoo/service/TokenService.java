package com.lehdonjoki.listoo.service;

import com.lehdonjoki.listoo.security.JwtTokenProvider;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;

    public TokenService(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public Map<String, String> generateTokens(String email) {
        String accessToken = jwtTokenProvider.generateAccessToken(email);
        String refreshToken = jwtTokenProvider.generateRefreshToken(email);

        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }

    public Map<String, String> refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken, true)) {
            throw new RuntimeException("Invalid refresh token.");
        }

        String email = jwtTokenProvider.extractEmail(refreshToken, true);
        return generateTokens(email);
    }
}
