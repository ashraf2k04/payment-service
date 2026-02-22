package com.ashraf.payment.controller;

import com.ashraf.payment.dto.*;
import com.ashraf.payment.iso.IsoDocument;
import com.ashraf.payment.service.Iso20022Service;
import com.ashraf.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;
    private final Iso20022Service isoService;

    @PostMapping(value = "/iso", consumes = "application/xml")
    public PaymentResponse processIso(@RequestBody String xml) {

        IsoDocument doc = isoService.parse(xml);

        BigDecimal amount = doc.getCreditTransfer()
                .getTransaction()
                .getAmountValue();

        String currency = doc.getCreditTransfer()
                .getTransaction()
                .getCurrency();

        PaymentRequest request = new PaymentRequest();
        request.setAmount(amount);
        request.setCurrency(currency);
        request.setReferenceId("ISO-" + System.currentTimeMillis());

        return service.createPayment(request);
    }

    @PostMapping
    public PaymentResponse create(@Valid @RequestBody PaymentRequest request) {
        return service.createPayment(request);
    }

    @GetMapping("/{id}")
    public PaymentResponse get(@PathVariable UUID id) {
        return service.getPayment(id);
    }

    @GetMapping
    public List<PaymentResponse> getAll() {
        return service.getAllPayments();
    }

    @PostMapping("/{id}/authorize")
    public PaymentResponse authorize(@PathVariable UUID id) {
        return service.authorizePayment(id);
    }

    @PostMapping("/{id}/capture")
    public PaymentResponse capture(@PathVariable UUID id) {
        return service.capturePayment(id);
    }

    @PostMapping("/{id}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public PaymentResponse refund(@PathVariable UUID id) {
        return service.refundPayment(id);
    }
}