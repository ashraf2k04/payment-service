package com.ashraf.payment.service;

import com.ashraf.payment.entity.User;
import com.ashraf.payment.entity.UserSession;
import com.ashraf.payment.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final UserSessionRepository repository;

    public UserSession createSession(User user, String refreshToken, long refreshExpirySeconds, String deviceName, String ip, String userAgent) {

        UserSession session = UserSession.builder()
                .user(user)
                .jti(UUID.randomUUID().toString())
                .refreshToken(refreshToken)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshExpirySeconds))
                .active(true)
                .deviceName(deviceName)
                .ipAddress(ip)
                .userAgent(userAgent)
                .build();

        return repository.save(session);
    }

    public UserSession validateRefreshToken(String refreshToken) {

        UserSession session = repository.findByRefreshTokenAndActiveTrue(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (!session.isValid()) {
            throw new RuntimeException("Session expired");
        }

        return session;
    }

    public void logout(String jti) {
        repository.findByJtiAndActiveTrue(jti)
                .ifPresent(UserSession::invalidate);
    }

    public void logoutAll(UUID userId) {
        List<UserSession> sessions = repository.findByUserIdAndActiveTrue(userId);
        sessions.forEach(UserSession::invalidate);
    }
}