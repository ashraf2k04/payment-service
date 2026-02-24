package com.ashraf.payment.entity;

public enum PaymentStatus {

    CREATED {
        @Override
        public boolean canAuthorize() { return false; }
    },

    AUTHORIZED {
        @Override
        public boolean canCapture() { return false; }
    },

    CAPTURED {
        @Override
        public boolean canRefund() { return false; }
    },

    FAILED,
    REFUNDED;

    public boolean canAuthorize() { return true; }
    public boolean canCapture() { return true; }
    public boolean canRefund() { return true; }

    public boolean isFinalState() {
        return this == FAILED || this == REFUNDED;
    }
}