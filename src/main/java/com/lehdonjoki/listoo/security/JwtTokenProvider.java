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
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.accessExpirationMs}")
    private long jwtAccessExpirationMs;

    @Value("${jwt.refreshExpirationMs}")
    private long jwtRefreshExpirationMs;

    private final RefreshTokenRepository refreshTokenRepository;

    public JwtTokenProvider(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
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

    /**
     * Rotates the refresh token by invalidating the old one and issuing a new token.
     * @param refreshTokenStr The old refresh token string.
     * @return A map containing the new access token and new refresh token.
     */
    @Transactional
    public Map<String, String> rotateRefreshToken(String refreshTokenStr) {
        RefreshToken oldRefreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token."));

        if (oldRefreshToken.getExpiryDate().before(new Date())) {
            refreshTokenRepository.delete(oldRefreshToken);
            throw new RuntimeException("Refresh token has expired. Please log in again.");
        }

        User user = oldRefreshToken.getUser();

        // Delete the old refresh token to invalidate it
        refreshTokenRepository.delete(oldRefreshToken);

        // Generate new tokens
        String newAccessToken = generateAccessToken(user.getEmail());
        RefreshToken newRefreshToken = generateRefreshToken(user);

        return Map.of(
                "accessToken", newAccessToken,
                "refreshToken", newRefreshToken.getToken()
        );
    }

    /**
     * Logs out the user by deleting all refresh tokens associated with them.
     * @param user The user to log out.
     */
    @Transactional
    public void logout(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
