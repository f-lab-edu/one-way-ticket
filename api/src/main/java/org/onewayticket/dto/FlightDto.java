package org.onewayticket.dto;

import lombok.Builder;
import org.onewayticket.domain.Flight;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public static FlightDto from(Flight flight) {
        return new FlightDto(flight.getId(), flight.getFlightNumber(), flight.getAmount(), flight.getDepartureTime(), flight.getArrivalTime(), flight.getOrigin(),
                flight.getDestination(), Duration.ofMinutes(flight.getDurationInMinutes()), flight.getCarrier());
    }

    public static List<FlightDto> from(List<Flight> flightList) {
        return flightList.stream()
                .map(FlightDto::from)
                .collect(Collectors.toList());
    }
}
