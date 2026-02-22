package com.ashraf.payment.security;

import com.ashraf.payment.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final String SECRET = "THIS_IS_A_SECRET_KEY_FOR_DEMO_PURPOSE_ONLY_123456";
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(UUID userId, String jti) {

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("jti", jti)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(getClaims(token).getSubject());
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public String extractJti(String token) {
        return getClaims(token).getId();
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
    }
}