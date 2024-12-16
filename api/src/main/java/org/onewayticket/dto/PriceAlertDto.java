package org.onewayticket.dto;

import java.math.BigDecimal;

public record PriceAlertDto(
        String username,
        String origin,
        String destination,
        BigDecimal targetAmount
) {
}
