package com.ashraf.payment.dto;

import com.ashraf.payment.entity.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Payment response object")
@Data
@Builder
public class PaymentResponse {

    @Schema(example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(example = "1000.00")
    @NotNull
    @Positive
    private BigDecimal amount;

    @Schema(example = "USD")
    @NotBlank
    private String currency;

    @Schema(example = "CREATED")
    private PaymentStatus status;

    @Schema(example = "ORDER-123")
    private String referenceId;

    @Schema(example = "2026-02-23T10:15:30")
    private LocalDateTime createdAt;

    @Schema(example = "2026-02-23T10:16:00")
    private LocalDateTime updatedAt;

    @Schema(example = "550e8400-e29b-41d4-a716-446655440111")
    private UUID userId;

    @Schema(example = "ashraf")
    private String username;
}