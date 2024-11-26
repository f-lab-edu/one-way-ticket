package org.onewayticket.dto;

import java.util.List;

public record BookingRequestDto(
        String bookingEmail,
        List<PassengerDto> passengers,
        String paymentKey // 결제 id
) {
}
