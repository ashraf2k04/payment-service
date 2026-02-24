package com.ashraf.payment.repository;

import com.ashraf.payment.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {

    Optional<UserSession> findByJtiAndActiveTrue(String jti);

    Optional<UserSession> findByRefreshTokenAndActiveTrue(String refreshToken);

    List<UserSession> findByUserIdAndActiveTrue(UUID userId);
}