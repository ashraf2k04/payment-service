
package com.ashraf.payment.service.impl;

import com.ashraf.payment.dto.PaymentRequest;
import com.ashraf.payment.dto.PaymentResponse;
import com.ashraf.payment.entity.Payment;
import com.ashraf.payment.entity.PaymentStatus;
import com.ashraf.payment.entity.User;
import com.ashraf.payment.exceptions.ResourceNotFoundException;
import com.ashraf.payment.mapper.PaymentMapper;
import com.ashraf.payment.repository.PaymentRepository;
import com.ashraf.payment.repository.UserRepository;
import com.ashraf.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log =
            LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository repository;
    private final UserRepository userRepository;

    private UUID getLoggedInUserId() {
        return UUID.fromString(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName()
        );
    }

    @Override
    public PaymentResponse createPayment(PaymentRequest request) {

        UUID userId = getLoggedInUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found while creating payment. UserId: {}", userId);
                    return new RuntimeException("User not found");
                });

        log.info("User {} creating payment with referenceId: {}",
                user.getUsername(), request.getReferenceId());

        Payment payment = PaymentMapper.toEntity(request);
        payment.setUser(user);

        Payment saved = repository.save(payment);

        log.info("Payment created successfully. PaymentId: {}, User: {}, Status: {}",
                saved.getId(), user.getUsername(), saved.getStatus());

        return PaymentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(UUID id) {

        Payment payment = validateOwnership(id);

        log.info("Payment fetched. PaymentId: {}, User: {}",
                payment.getId(),
                payment.getUser().getUsername());

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
                .collect(Collectors.toList());
    }

    @Override
    public PaymentResponse authorizePayment(UUID id) {

        Payment payment = validateOwnership(id);

        if (payment.getStatus() == PaymentStatus.AUTHORIZED) {
            log.info("Payment already authorized. PaymentId: {}", id);
            throw new RuntimeException("Payment already authorized");
        }

        if (payment.getStatus() != PaymentStatus.CREATED) {
            log.error("Invalid authorize attempt. PaymentId: {}, CurrentStatus: {}",
                    id, payment.getStatus());
            throw new RuntimeException("Payment must be in CREATED state to authorize");
        }

        payment.setStatus(PaymentStatus.AUTHORIZED);

        log.info("Payment authorized. PaymentId: {}", id);

        return PaymentMapper.toResponse(repository.save(payment));
    }

    @Override
    public PaymentResponse capturePayment(UUID id) {

        Payment payment = validateOwnership(id);

        if (payment.getStatus() == PaymentStatus.CAPTURED) {
            log.info("Payment already captured. PaymentId: {}", id);
            throw new RuntimeException("Payment already captured");
        }

        if (payment.getStatus() != PaymentStatus.AUTHORIZED) {
            log.error("Invalid capture attempt. PaymentId: {}, CurrentStatus: {}",
                    id, payment.getStatus());
            throw new RuntimeException("Payment must be AUTHORIZED before capture");
        }

        payment.setStatus(PaymentStatus.CAPTURED);

        log.info("Payment captured. PaymentId: {}", id);

        return PaymentMapper.toResponse(repository.save(payment));
    }

    @Override
    public PaymentResponse refundPayment(UUID id) {

        Payment payment = validateOwnership(id);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("Authorities: {}", auth.getAuthorities());

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            log.info("Payment already refunded. PaymentId: {}", id);
            throw new RuntimeException("Payment already refunded");
        }

        if (payment.getStatus() != PaymentStatus.CAPTURED) {
            log.error("Invalid refund attempt. PaymentId: {}, CurrentStatus: {}",
                    id, payment.getStatus());
            throw new RuntimeException("Payment must be CAPTURED before refund");
        }

        payment.setStatus(PaymentStatus.REFUNDED);

        log.info("Payment refunded. PaymentId: {}", id);

        return PaymentMapper.toResponse(repository.save(payment));
    }

    private Payment validateOwnership(UUID id) {

        UUID userId = getLoggedInUserId();

        Payment payment = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Payment not found. PaymentId: {}", id);
                    return new ResourceNotFoundException("Payment not found");
                });

        if (!payment.getUser().getId().equals(userId)) {
            log.error("Unauthorized access attempt. UserId: {}, PaymentId: {}",
                    userId, id);
            throw new RuntimeException("Access denied");
        }

        return payment;
    }
}
