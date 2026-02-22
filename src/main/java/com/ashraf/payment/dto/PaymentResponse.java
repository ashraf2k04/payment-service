package com.ashraf.payment.dto;

import com.ashraf.payment.entity.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PaymentResponse {

    private UUID id;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotBlank
    private String currency;

    private PaymentStatus status;
    private String referenceId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private UUID userId;
    private String username;
}