package org.onewayticket.dto;


import java.math.BigDecimal;

public record SaveAmountRequestDto(
        String orderId,
        BigDecimal amount
) {
}
