package com.ashraf.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Payment creation request")
public record PaymentRequest(

        @Schema(example = "1000.00", description = "Payment amount")
        @NotNull @Positive
        BigDecimal amount,

        @Schema(example = "USD", description = "Currency code")
        @NotBlank
        String currency,

        @Schema(example = "ORDER-123", description = "Merchant reference ID")
        @NotBlank
        String referenceId
) {}