package com.ashraf.payment.mapper;

import com.ashraf.payment.dto.PaymentRequest;
import com.ashraf.payment.dto.PaymentResponse;
import com.ashraf.payment.entity.Payment;

public class PaymentMapper {

    public static Payment toEntity(PaymentRequest request) {
        return Payment.builder()
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .referenceId(request.getReferenceId())
                .build();
    }

    public static PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .referenceId(payment.getReferenceId())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .userId(payment.getUser().getId())
                .username(payment.getUser().getUsername())
                .build();
    }
}