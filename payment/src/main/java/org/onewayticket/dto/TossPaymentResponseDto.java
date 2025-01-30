package org.onewayticket.dto;

import org.onewayticket.domain.TossPayment;
import org.onewayticket.enums.PaymentMethod;
import org.onewayticket.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @param paymentKey 결제 키 값
 * @param orderId Toss 결제 주문번호
 * @param method 결제수단
 * @param currency 통화
 * @param totalAmount 총 결제액
 */
public record TossPaymentResponseDto(
        String paymentKey,
        String orderId,
        PaymentMethod method,
        PaymentStatus status,
        String currency,
        BigDecimal totalAmount,
        LocalDateTime approvedAt
) {
    public static TossPaymentResponseDto from(TossPayment tossPayment){
        return new TossPaymentResponseDto(
                tossPayment.getTossPaymentKey(),
                tossPayment.getTossOrderId(),
                tossPayment.getPaymentMethod(),
                tossPayment.getPaymentStatus(),
                tossPayment.getCurrency(),
                tossPayment.getTotalAmount(),
                tossPayment.getApprovedAt()
        );
    }
}
