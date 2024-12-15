package org.onewayticket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PaymentConfirmRequestDto(
        @NotBlank(message = "orderId는 필수값입니다.")
        @Size(min = 6, max = 64, message = "orderId는 6자리 이상 64자리 이하여야 합니다.")
        String orderId,

        @NotBlank(message = "결제 금액은 필수값입니다.")
        @PositiveOrZero(message = "결제 금액은 양수여야합니다.")
        BigDecimal amount,

        @NotBlank(message = "paymentKey는 필수값입니다.")
        String paymentKey
) {
}
