package org.onewayticket.dto;

import java.math.BigDecimal;

public record PaymentRequestDto(
        String orderId,
        BigDecimal amount
) {
}
