package com.ashraf.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;


@Schema(description = "Payment creation request")
@Data
public class PaymentRequest {

    @Schema(example = "1000.00", description = "Payment amount")
    private BigDecimal amount;

    @Schema(example = "USD", description = "Currency code")
    private String currency;

    @Schema(example = "ORDER-123", description = "Merchant reference ID")
    private String referenceId;
}