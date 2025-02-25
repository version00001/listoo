package com.lehdonjoki.listoo.security;

import com.lehdonjoki.listoo.model.RefreshToken;
import com.lehdonjoki.listoo.model.User;
import com.lehdonjoki.listoo.repository.RefreshTokenRepository;
import com.lehdonjoki.listoo.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

// ----------------------------
// JwtTokenProvider.java
// ----------------------------
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.accessExpirationMs}")
    private int jwtAccessExpirationMs;

    @Value("${jwt.refreshExpirationMs}")
    private int jwtRefreshExpirationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public JwtTokenProvider(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public String generateAccessToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtAccessExpirationMs);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    @Transactional
    public RefreshToken generateRefreshToken(User user) {
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(new Date(System.currentTimeMillis() + jwtRefreshExpirationMs));

        return refreshTokenRepository.save(refreshToken);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public String refreshAccessToken(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token."));

        if (refreshToken.getExpiryDate().before(new Date())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token has expired. Please log in again.");
        }

        return generateAccessToken(refreshToken.getUser().getEmail());
    }
}
