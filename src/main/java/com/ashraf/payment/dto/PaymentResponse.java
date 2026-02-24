package com.ashraf.payment.dto;

import com.ashraf.payment.entity.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Payment response object")
public record PaymentResponse(

        @Schema(example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(example = "1000.00")
        BigDecimal amount,

        @Schema(example = "USD")
        String currency,

        @Schema(example = "CREATED")
        PaymentStatus status,

        @Schema(example = "ORDER-123")
        String referenceId,

        @Schema(example = "2026-02-23T10:15:30")
        LocalDateTime createdAt,

        @Schema(example = "2026-02-23T10:16:00")
        LocalDateTime updatedAt,

        @Schema(example = "550e8400-e29b-41d4-a716-446655440111")
        UUID userId,

        @Schema(example = "ashraf")
        String username
) {}