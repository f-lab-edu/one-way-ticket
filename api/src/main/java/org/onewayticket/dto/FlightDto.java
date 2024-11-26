package org.onewayticket.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Builder
public record FlightDto(
        Long id,                      // 항공편 id
        String flightNumber,          // 항공편명
        BigDecimal amount,                   // 항공권 가격
        LocalDateTime departureTime,  // 출발 시간
        LocalDateTime arrivalTime,    // 도착 시간
        String origin,                // 출발 공항 코드
        String destination,           // 도착 공항 코드
        Duration flightDuration,       // 소요 시간
        String carrier                // 항공사
) {

}
