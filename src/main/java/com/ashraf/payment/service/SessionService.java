package com.ashraf.payment.service;

import com.ashraf.payment.entity.User;
import com.ashraf.payment.entity.UserSession;
import com.ashraf.payment.exceptions.SessionAlreadyActiveException;
import com.ashraf.payment.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final UserSessionRepository repository;

    public String createSession(User user) {

        String jti = UUID.randomUUID().toString();

        UserSession session = UserSession.builder()
                .user(user)
                .jti(jti)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(session);

        return jti;
    }

    public void invalidateSession(String jti) {

        UserSession session = repository.findByJtiAndActiveTrue(jti)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        session.setActive(false);
        session.setExpiredAt(LocalDateTime.now());

        repository.save(session);
    }

    public boolean isSessionActive(String jti) {
        return repository.findByJtiAndActiveTrue(jti).isPresent();
    }
}