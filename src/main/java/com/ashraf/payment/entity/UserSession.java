package com.ashraf.payment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_sessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String jti;

    @Column(nullable = false, unique = true)
    private String refreshToken;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime invalidatedAt;

    @Version
    private Long version;

    @Builder.Default
    @Column(nullable = false)
    private Boolean refreshTokenUsed = false;

    @Column(length = 100)
    private String deviceName;

    @Column(length = 45)
    private String ipAddress;

    @Column(length = 255)
    private String userAgent;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.active = true;
        this.refreshTokenUsed = false;
    }

    public void markRefreshTokenUsed() {
        this.refreshTokenUsed = true;
    }

    public void invalidate() {
        this.active = false;
        this.invalidatedAt = LocalDateTime.now();
    }

    public boolean isValid() {
        return active && expiresAt.isAfter(LocalDateTime.now());
    }

    public void rotateRefreshToken(String newToken, LocalDateTime newExpiry) {
        this.refreshToken = newToken;
        this.expiresAt = newExpiry;
    }
}