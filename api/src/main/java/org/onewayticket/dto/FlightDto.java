package org.onewayticket.dto;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

public record FlightDto(
        String flightId,              // 항공편명
        BigDecimal price,                   // 항공권 가격
        LocalDateTime departureDate,  // 출발 날짜
        String origin,                // 출발 공항 코드
        String destination,           // 도착 공항 코드
        LocalDateTime departureTime,  // 출발 시간
        LocalDateTime arrivalTime,    // 도착 시간
        Duration flightDuration       // 소요 시간
) {

}
