package com.ashraf.payment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false, unique = true, length = 100)
    private String referenceId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version; // 🔥 prevents double processing


    public static Payment create(
            BigDecimal amount,
            String currency,
            String referenceId,
            User user
    ) {
        return Payment.builder()
                .amount(amount)
                .currency(currency)
                .referenceId(referenceId)
                .user(user)
                .status(PaymentStatus.CREATED)
                .build();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void authorize() {
        if (status.canAuthorize()) {
            throw new IllegalStateException(
                    "Cannot authorize payment in state %s"
                            .formatted(status)
            );
        }
        this.status = PaymentStatus.AUTHORIZED;
    }

    public void capture() {
        if (status.canCapture()) {
            throw new IllegalStateException(
                    "Cannot capture payment in state %s"
                            .formatted(status)
            );
        }
        this.status = PaymentStatus.CAPTURED;
    }

    public void refund() {
        if (status.canRefund()) {
            throw new IllegalStateException(
                    "Cannot refund payment in state %s"
                            .formatted(status)
            );
        }
        this.status = PaymentStatus.REFUNDED;
    }

    public void assignUser(User user) {
        this.user = user;
    }
}