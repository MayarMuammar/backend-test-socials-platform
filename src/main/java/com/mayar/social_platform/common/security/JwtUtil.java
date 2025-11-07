package com.mayar.social_platform.common.security;


import com.mayar.social_platform.modules.user.entity.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    private final String jwtSecret;
    private final long jwtExpirationInMs;

    public JwtUtil(@Value("${jwt.secret}") String jwtSecret, @Value("${jwt.expiration-ms:86400000}")long jwtExpirationInMs) {
        this.jwtSecret = jwtSecret;
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    public String generateToken(String userId, String username, UserRole role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId);
        claims.put("username", username);
        claims.put("role", role);

        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .claims(claims)
                .subject(userId)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getRoleFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("role", String.class);
    }

    public String getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();  // Subject = userId
    }


    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
