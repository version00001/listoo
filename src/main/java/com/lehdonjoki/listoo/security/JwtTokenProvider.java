package com.lehdonjoki.listoo.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j // ✅ Auto-generates the logger
@Component
public class JwtTokenProvider {

  private final Key ACCESS_SECRET_KEY;
  private final Key REFRESH_SECRET_KEY;

  private static final long ACCESS_EXPIRATION_TIME = 15L * 60 * 1000; // 15 minutes
  private static final long REFRESH_EXPIRATION_TIME = 30L * 24 * 60 * 60 * 1000; // 30 days

  public JwtTokenProvider(
      @Value("${jwt.access.secret}") String accessSecret,
      @Value("${jwt.refresh.secret}") String refreshSecret) {
    this.ACCESS_SECRET_KEY = Keys.hmacShaKeyFor(Base64.getDecoder().decode(accessSecret));
    this.REFRESH_SECRET_KEY = Keys.hmacShaKeyFor(Base64.getDecoder().decode(refreshSecret));
  }

  public String generateAccessToken(String email) {
    return Jwts.builder()
        .setSubject(email)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION_TIME))
        .signWith(ACCESS_SECRET_KEY, SignatureAlgorithm.HS512)
        .compact();
  }

  public String generateRefreshToken(String email) {
    return Jwts.builder()
        .setSubject(email)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME))
        .signWith(REFRESH_SECRET_KEY, SignatureAlgorithm.HS512)
        .compact();
  }

  public boolean validateToken(String token, boolean isRefreshToken) {
    try {
      Key signingKey = isRefreshToken ? REFRESH_SECRET_KEY : ACCESS_SECRET_KEY;
      Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public String extractEmail(String token, boolean isRefreshToken) {
    Key signingKey = isRefreshToken ? REFRESH_SECRET_KEY : ACCESS_SECRET_KEY;
    return Jwts.parserBuilder()
        .setSigningKey(signingKey)
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }
}
