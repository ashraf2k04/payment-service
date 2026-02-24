package com.ashraf.payment.service;

import com.ashraf.payment.config.JwtProperties;
import com.ashraf.payment.dto.*;
import com.ashraf.payment.entity.User;
import com.ashraf.payment.entity.UserRole;
import com.ashraf.payment.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SessionService sessionService;
    private final JwtProperties properties;

    public void register(RegisterRequest request) {

        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        userRepository.save(User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(UserRole.ROLE_USER)
                .build());
    }

    public AuthResponse login(
            AuthRequest request,
            HttpServletRequest httpRequest
    ) {

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String refreshToken = jwtService.generateRefreshToken(user.getId());

        String deviceName = httpRequest.getHeader("X-Device-Name");
        String userAgent = httpRequest.getHeader("User-Agent");
        String ip = httpRequest.getRemoteAddr();


        var session = sessionService.createSession(
                user,
                refreshToken,
                properties.refreshExpiration(),
                deviceName,
                ip,
                userAgent
        );

        String accessToken = jwtService.generateAccessToken(user.getId(), session.getJti());

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refresh(String refreshToken) {

        var session = sessionService.validateRefreshToken(refreshToken);

        // 🚨 REUSE DETECTION
        if (session.getRefreshTokenUsed()) {

            // Invalidate ALL sessions for security
            sessionService.logoutAll(session.getUser().getId());

            throw new SecurityException("Refresh token reuse detected");
        }

        // Mark current refresh token as used
        session.markRefreshTokenUsed();

        // Generate new refresh token
        String newRefresh = jwtService.generateRefreshToken(session.getUser().getId());

        // Sliding session logic
        session.rotateRefreshToken(
                newRefresh,
                LocalDateTime.now().plusSeconds(properties.refreshExpiration())
        );

        String newAccess =
                jwtService.generateAccessToken(session.getUser().getId(), session.getJti());

        return new AuthResponse(newAccess, newRefresh);
    }

    public void logout(String accessToken) {
        String jti = jwtService.extractJti(accessToken);
        sessionService.logout(jti);
    }

    public void logoutAll(String accessToken) {
        var userId = jwtService.extractUserId(accessToken);
        sessionService.logoutAll(userId);
    }
}