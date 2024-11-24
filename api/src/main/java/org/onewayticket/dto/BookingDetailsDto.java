package org.onewayticket.dto;

import java.util.List;

public record BookingDetailsDto(
        String referenceCode,                       // 예약 번호(사용자 조회용)
        String bookingEmail,                // 예약자 이메일
        FlightDto flightDto,                // 항공편 정보
        List<PassengerDto> passengerDtoList// 탑승자 정보
) {
}
