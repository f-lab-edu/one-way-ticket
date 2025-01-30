package org.onewayticket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 최초 결제 금액을 세션에 저장하기 위한 Dto
 */
public record PaymentRequestDto(
        @NotBlank(message = "orderId는 필수값입니다.")
        @Size(min = 6, max = 64, message = "orderId는 6자리 이상 64자리 이하여야 합")
        Long orderId,

        @NotNull(message = "결제 금액은 필수값입니다.")
        @PositiveOrZero(message = "결제 금액은 양수여야합니다.")
        BigDecimal amount
) {
}
