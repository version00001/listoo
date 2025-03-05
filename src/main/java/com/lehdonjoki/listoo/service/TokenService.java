package com.lehdonjoki.listoo.service;

import com.lehdonjoki.listoo.model.RefreshToken;
import com.lehdonjoki.listoo.model.User;
import com.lehdonjoki.listoo.security.JwtTokenProvider;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

  private final JwtTokenProvider jwtTokenProvider;

  public TokenService(JwtTokenProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }

  public Map<String, String> generateTokens(User user) {
    String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
    RefreshToken refreshToken = jwtTokenProvider.generateRefreshToken(user);

    return Map.of("accessToken", accessToken, "refreshToken", refreshToken.getToken());
  }

  public Map<String, String> refreshToken(String refreshTokenStr) {
    if (!jwtTokenProvider.validateToken(refreshTokenStr)) {
      throw new RuntimeException("Invalid refresh token.");
    }

    return jwtTokenProvider.rotateRefreshToken(refreshTokenStr);
  }

  public void logout(User user) {
    jwtTokenProvider.logout(user);
  }
}
