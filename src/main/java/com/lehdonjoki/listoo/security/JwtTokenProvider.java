package com.lehdonjoki.listoo.security;

import com.shoppinglistapp.model.RefreshToken;
import com.shoppinglistapp.model.User;
import com.shoppinglistapp.repository.RefreshTokenRepository;
import com.shoppinglistapp.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

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

    /**
     * Generates an access token for the authenticated user.
     * @param authentication The authentication object.
     * @return A signed JWT access token.
     */
    public String generateAccessToken(Authentication authentication) {
        String email = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtAccessExpirationMs);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    /**
     * Generates a new refresh token for a user.
     * @param user The user entity.
     * @return The newly generated refresh token.
     */
    @Transactional
    public RefreshToken generateRefreshToken(User user) {
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser(user);
        existingToken.ifPresent(refreshTokenRepository::delete); // Remove old token if exists

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(new Date(System.currentTimeMillis() + jwtRefreshExpirationMs));

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Validates a given JWT token.
     * @param token The JWT token.
     * @return true if valid, false otherwise.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extracts the email from a JWT token.
     * @param token The JWT token.
     * @return The email address from the token.
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * Refreshes the access token using a valid refresh token.
     * @param refreshTokenStr The refresh token string.
     * @return A new access token.
     */
    public String refreshAccessToken(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token."));

        if (refreshToken.getExpiryDate().before(new Date())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token has expired. Please log in again.");
        }

        User user = refreshToken.getUser();
        return generateAccessToken(user.getEmail());
    }

    /**
     * Generates an access token directly from an email address (used during refresh).
     * @param email The user's email.
     * @return A signed JWT access token.
     */
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
}
