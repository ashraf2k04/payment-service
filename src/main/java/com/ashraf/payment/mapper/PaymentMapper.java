package com.ashraf.payment.mapper;

import com.ashraf.payment.dto.PaymentResponse;
import com.ashraf.payment.entity.Payment;

public final class PaymentMapper {

    private PaymentMapper() {}

    public static PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getReferenceId(),
                payment.getCreatedAt(),
                payment.getUpdatedAt(),
                payment.getUser().getId(),
                payment.getUser().getUsername()
        );
    }
}