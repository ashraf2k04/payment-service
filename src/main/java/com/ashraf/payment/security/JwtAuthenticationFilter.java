package com.ashraf.payment.security;

import com.ashraf.payment.entity.User;
import com.ashraf.payment.repository.UserRepository;
import com.ashraf.payment.service.JwtService;
import com.ashraf.payment.repository.UserSessionRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserSessionRepository sessionRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        // ✅ Validate JWT signature + expiration
        if (!jwtService.isValid(token)) {
            throw new BadCredentialsException("Invalid or expired token");
        }

        Claims claims = jwtService.parse(token);

        String jti = claims.getId();
        UUID userId = UUID.fromString(claims.getSubject());

        // ✅ Check session in DB
        var session = sessionRepository
                .findByJtiAndActiveTrue(jti)
                .orElse(null);

        if (session == null || !session.isValid()) {
            throw new BadCredentialsException("Session Invalid!");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        user.getId().toString(),
                        null,
                        List.of(new SimpleGrantedAuthority(user.getRole().name()))
                );

        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }
}