package org.onewayticket.dto;

public record PaymentConfirmRequestDto(
        String orderId,
        String amount,
        String paymentKey
) {
}
