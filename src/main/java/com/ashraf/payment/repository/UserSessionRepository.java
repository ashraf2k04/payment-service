package com.ashraf.payment.repository;

import com.ashraf.payment.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {

    Optional<UserSession> findByJtiAndActiveTrue(String jti);
}