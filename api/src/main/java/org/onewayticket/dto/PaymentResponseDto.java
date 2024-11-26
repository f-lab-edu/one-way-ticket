package org.onewayticket.dto;

public record PaymentResponseDto(
        String paymentKey,
        String orderId,
        String method,
        String currency,
        String totalAmount
) {
}
