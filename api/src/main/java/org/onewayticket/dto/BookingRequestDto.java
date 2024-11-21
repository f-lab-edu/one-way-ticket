package org.onewayticket.dto;

public record BookingRequestDto(
        String bookingName,
        String bookingEmail,
        String bookingPhoneNumber,
        String flightId, // 항공편명
        String birthDate,
        String paymentId // 결제 id
) {
}
