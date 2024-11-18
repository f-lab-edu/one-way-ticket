package org.onewayticket.dto;

public record BookingDto(
        String bookingId,         // 예약 ID
        String reservationName,   // 예약자 이름
        String reservationEmail,  // 예약자 이메일
        String reservationPhoneNumber // 예약자 전화번호
) {
}
