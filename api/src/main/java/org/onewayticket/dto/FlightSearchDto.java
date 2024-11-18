package org.onewayticket.dto;

import java.time.LocalDateTime;

public record FlightSearchDto(
        String departure,       // 출발지
        String destination,     // 목적지
        LocalDateTime departureDate, // 출발일
        Integer numberOfPassengers // 탑승 인원
) {
}
