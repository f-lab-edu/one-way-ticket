package org.onewayticket.service;


import lombok.RequiredArgsConstructor;
import org.onewayticket.domain.Payment;
import org.onewayticket.dto.PaymentCreatedEvent;
import org.onewayticket.enums.PaymentStatus;
import org.onewayticket.repository.PaymentRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Payment createPayment(String username, Long orderId, BigDecimal amount) {
        Payment payment = Payment.builder()
                .username(username)
                .amount(amount)
                .orderId(orderId)
                .status(PaymentStatus.CREATED)
                .build();
        paymentRepository.save(payment);

        eventPublisher.publishEvent(new PaymentCreatedEvent(payment.getId()));

        return payment;
    }

    @Transactional
    public void updatePaymentStatus(Long paymentId, PaymentStatus status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found."));

        payment.setStatus(status);
        paymentRepository.save(payment);
    }

    public Payment getPaymentStatus(Long paymentId) {
        return paymentRepository.findById(paymentId).orElseThrow();
    }
}