package com.ashraf.payment.service;

import com.ashraf.payment.dto.PaymentRequest;
import com.ashraf.payment.dto.PaymentResponse;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    PaymentResponse createPayment(PaymentRequest request);

    PaymentResponse getPayment(UUID id);

    List<PaymentResponse> getAllPayments();

    PaymentResponse authorizePayment(UUID id);

    PaymentResponse capturePayment(UUID id);

    PaymentResponse refundPayment(UUID id);
}