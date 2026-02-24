package com.ashraf.payment.service;

import com.ashraf.payment.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties properties;

    private Key getKey() {
        return Keys.hmacShaKeyFor(properties.secret().getBytes());
    }

    public String generateAccessToken(UUID userId, String jti) {

        Instant now = Instant.now();

        return Jwts.builder()
                .setSubject(userId.toString())
                .setId(jti)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(properties.accessExpiration())))
                .signWith(getKey())
                .compact();
    }

    public String generateRefreshToken(UUID userId) {

        Instant now = Instant.now();

        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(properties.refreshExpiration())))
                .signWith(getKey())
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(parse(token).getSubject());
    }

    public String extractJti(String token) {
        return parse(token).getId();
    }

    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}