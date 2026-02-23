package com.ashraf.payment.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private static final String SECRET =
            "THIS_IS_A_SECRET_KEY_FOR_DEMO_PURPOSE_ONLY_123456";
    private static final Key key =
            Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(UUID userId, String jti) {

        var now = Instant.now();

        return Jwts.builder()
                .setSubject(userId.toString())
                .setId(jti) // ✅ proper JTI usage
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(3600)))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(getClaims(token).getSubject());
    }

    public String extractJti(String token) {
        return getClaims(token).getId(); // now correct
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}