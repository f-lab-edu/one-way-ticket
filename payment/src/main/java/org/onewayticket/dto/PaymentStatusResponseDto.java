package org.onewayticket.dto;

import org.onewayticket.domain.Payment;
import org.onewayticket.enums.PaymentStatus;

public record PaymentStatusResponseDto(
        Long paymentId,
        PaymentStatus paymentStatus
) {
    public static PaymentStatusResponseDto from(Payment payment){
        return new PaymentStatusResponseDto(payment.getId(), payment.getStatus());
    }
}
