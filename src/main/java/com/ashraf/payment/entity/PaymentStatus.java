package com.ashraf.payment.entity;

public enum PaymentStatus {
    CREATED,
    AUTHORIZED,
    CAPTURED,
    FAILED,
    REFUNDED;

    public boolean isFinalState() {
        return this == FAILED || this == REFUNDED;
    }
}