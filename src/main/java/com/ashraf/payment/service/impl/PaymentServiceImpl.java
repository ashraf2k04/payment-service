package com.ashraf.payment.service.impl;

import com.ashraf.payment.dto.PaymentRequest;
import com.ashraf.payment.dto.PaymentResponse;
import com.ashraf.payment.entity.Payment;
import com.ashraf.payment.entity.User;
import com.ashraf.payment.exceptions.ResourceNotFoundException;
import com.ashraf.payment.mapper.PaymentMapper;
import com.ashraf.payment.repository.PaymentRepository;
import com.ashraf.payment.repository.UserRepository;
import com.ashraf.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;
    private final UserRepository userRepository;

    private UUID getLoggedInUserId() {
        return UUID.fromString(
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName()
        );
    }

    @Override
    public PaymentResponse createPayment(PaymentRequest request) {

        UUID userId = getLoggedInUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User %s not found".formatted(userId)
                ));

        log.info("User {} creating payment with referenceId: {}",
                user.getUsername(), request.referenceId());

        Payment payment = Payment.create(
                request.amount(),
                request.currency(),
                request.referenceId(),
                user
        );

        Payment saved = repository.save(payment);

        log.info("Payment created successfully. PaymentId: {}, Status: {}",
                saved.getId(), saved.getStatus());

        return PaymentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(UUID id) {
        Payment payment = validateOwnership(id);

        log.info("Payment fetched. PaymentId: {}", id);

        return PaymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {

        UUID userId = getLoggedInUserId();

        log.info("Fetching all payments for userId: {}", userId);

        return repository.findByUserId(userId)
                .stream()
                .map(PaymentMapper::toResponse)
                .toList(); // Java 16+
    }

    @Override
    public PaymentResponse authorizePayment(UUID id) {
        Payment payment = validateOwnership(id);

        if (payment.getStatus().canAuthorize()) {
            throw new IllegalStateException(
                    "Cannot authorize payment in state %s"
                            .formatted(payment.getStatus())
            );
        }

        payment.authorize();

        log.info("Payment authorized. PaymentId: {}", id);

        return PaymentMapper.toResponse(repository.save(payment));
    }

    @Override
    public PaymentResponse capturePayment(UUID id) {
        Payment payment = validateOwnership(id);

        if (payment.getStatus().canCapture()) {
            throw new IllegalStateException(
                    "Cannot capture payment in state %s"
                            .formatted(payment.getStatus())
            );
        }

        payment.capture();

        log.info("Payment captured. PaymentId: {}", id);

        return PaymentMapper.toResponse(repository.save(payment));
    }

    @Override
    public PaymentResponse refundPayment(UUID id) {
        Payment payment = validateOwnership(id);

        if (payment.getStatus().canRefund()) {
            throw new IllegalStateException(
                    "Cannot refund payment in state %s"
                            .formatted(payment.getStatus())
            );
        }

        payment.refund();

        log.info("Payment refunded. PaymentId: {}", id);

        return PaymentMapper.toResponse(repository.save(payment));
    }

    private Payment validateOwnership(UUID id) {

        UUID userId = getLoggedInUserId();

        Payment payment = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment %s not found".formatted(id)
                ));

        if (!payment.getUser().getId().equals(userId)) {
            throw new IllegalStateException(
                    "User %s is not owner of payment %s"
                            .formatted(userId, id)
            );
        }

        return payment;
    }
}