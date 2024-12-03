package org.onewayticket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record BookingRequestDto(
        @NotBlank
        @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])+[.][a-zA-Z]{2,3}$", message = "이메일 양식을 확인해주세요")
        String bookingEmail,

        List<PassengerDto> passengers,

        @NotBlank(message = "paymentKey가 누락되었습니다.")
        String paymentKey // 결제 id
) {
}
