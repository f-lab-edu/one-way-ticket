package org.onewayticket.dto;

import java.util.List;

public record BookingRequestDto(
        String bookingEmail,
        List<PassengerDto> passengers,
        String flightNumber, // 항공편명
        String paymentId // 결제 id
) {
}
