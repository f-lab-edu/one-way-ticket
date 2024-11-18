package org.onewayticket.dto;

public record BookingRequestDto(
        String bookingName,
        String bookingEmail,
        String bookingPhoneNumber,
        String birthDate,
        String flightId, // 항공편명
        String paymentId // 결제 id
) {
}
