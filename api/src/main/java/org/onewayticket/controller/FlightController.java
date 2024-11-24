package org.onewayticket.controller;

import lombok.RequiredArgsConstructor;
import org.onewayticket.dto.FlightDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/flights")
public class FlightController {

    @GetMapping("/cheapest")
    public ResponseEntity<List<FlightDto>> getCheapestFlights() {
        // 더미 데이터 9개 생성
        List<FlightDto> flights = List.of(
                new FlightDto(1L, "FL001", BigDecimal.valueOf(50),
                        LocalDateTime.of(2024, 11, 24, 8, 0), // 출발 시간
                        LocalDateTime.of(2024, 11, 24, 10, 0), // 도착 시간
                        "ICN", "NRT", Duration.ofHours(2), "Korean Air"),
                new FlightDto(2L, "FL002", BigDecimal.valueOf(70),
                        LocalDateTime.of(2024, 11, 24, 9, 0),
                        LocalDateTime.of(2024, 11, 24, 11, 0),
                        "ICN", "HND", Duration.ofHours(2), "Asiana Airlines"),
                new FlightDto(3L, "FL003", BigDecimal.valueOf(80),
                        LocalDateTime.of(2024, 11, 24, 10, 0),
                        LocalDateTime.of(2024, 11, 24, 12, 0),
                        "ICN", "KIX", Duration.ofHours(2), "Korean Air"),
                new FlightDto(4L, "FL004", BigDecimal.valueOf(100),
                        LocalDateTime.of(2024, 11, 24, 11, 0),
                        LocalDateTime.of(2024, 11, 24, 22, 0),
                        "ICN", "LAX", Duration.ofHours(11), "Delta Air Lines"),
                new FlightDto(5L, "FL005", BigDecimal.valueOf(120),
                        LocalDateTime.of(2024, 11, 24, 12, 0),
                        LocalDateTime.of(2024, 11, 25, 2, 0),
                        "ICN", "JFK", Duration.ofHours(14), "American Airlines"),
                new FlightDto(6L, "FL006", BigDecimal.valueOf(150),
                        LocalDateTime.of(2024, 11, 24, 13, 0),
                        LocalDateTime.of(2024, 11, 24, 23, 0),
                        "ICN", "CDG", Duration.ofHours(10), "Air France"),
                new FlightDto(7L, "FL007", BigDecimal.valueOf(200),
                        LocalDateTime.of(2024, 11, 24, 14, 0),
                        LocalDateTime.of(2024, 11, 25, 0, 0),
                        "ICN", "SYD", Duration.ofHours(10), "Qantas"),
                new FlightDto(8L, "FL008", BigDecimal.valueOf(300),
                        LocalDateTime.of(2024, 11, 24, 15, 0),
                        LocalDateTime.of(2024, 11, 25, 4, 0),
                        "ICN", "FRA", Duration.ofHours(13), "Lufthansa"),
                new FlightDto(9L, "FL009", BigDecimal.valueOf(350),
                        LocalDateTime.of(2024, 11, 24, 16, 0),
                        LocalDateTime.of(2024, 11, 24, 22, 0),
                        "ICN", "SIN", Duration.ofHours(6), "Singapore Airlines")
        );


        // 가격 기준으로 정렬 후 상위 9개 반환
        return ResponseEntity.ok(flights.stream()
                .sorted((f1, f2) -> f1.amount().compareTo(f2.amount()))
                .limit(9)
                .toList());
    }

    // 검색결과 보여줄 필터 추가
    @GetMapping("/search")
    public ResponseEntity<List<FlightDto>> searchFlights(
            @RequestParam("departure") String departure,
            @RequestParam("destination") String destination,
            @RequestParam("departureDate") String departureDate,
            @RequestParam("numberOfPassengers") Integer numberOfPassengers,
            @RequestParam(value = "sort", defaultValue = "price") String sort) {

        // 출발지와 목적지가 같은 경우
        if (departure.equalsIgnoreCase(destination)) {
            return ResponseEntity.badRequest().body(List.of());
        }

        // 예제 데이터 생성
        List<FlightDto> flights = List.of(
                new FlightDto(7L, "FL007", BigDecimal.valueOf(200),
                        LocalDateTime.of(2024, 11, 24, 14, 0),
                        LocalDateTime.of(2024, 11, 25, 0, 0),
                        "ICN", "SYD", Duration.ofHours(10), "Qantas"),
                new FlightDto(8L, "FL008", BigDecimal.valueOf(300),
                        LocalDateTime.of(2024, 11, 24, 15, 0),
                        LocalDateTime.of(2024, 11, 25, 4, 0),
                        "ICN", "FRA", Duration.ofHours(13), "Lufthansa"),
                new FlightDto(9L, "FL009", BigDecimal.valueOf(350),
                        LocalDateTime.of(2024, 11, 24, 16, 0),
                        LocalDateTime.of(2024, 11, 24, 22, 0),
                        "ICN", "SIN", Duration.ofHours(6), "Singapore Airlines")
        );

        // 정렬 로직 추가
        List<FlightDto> sortedFlights = flights.stream()
                .sorted((f1, f2) -> {
                    switch (sort) {
                        case "price":
                        default:
                            return f1.amount().compareTo(f2.amount());
                        case "arrivalTime":
                            return f1.arrivalTime().compareTo(f2.arrivalTime());
                        case "flightDuration":
                            return f1.flightDuration().compareTo(f2.flightDuration());

                    }
                })
                .toList();

        return ResponseEntity.ok(sortedFlights);
    }


    @GetMapping("/{flightId}")
    public ResponseEntity<FlightDto> getFlightDetails(@PathVariable String flightId) {
        if (flightId.length() > 50) { // flightId 유효성 검사
            return ResponseEntity.badRequest().build();
        }

        if (1L == Long.parseLong(flightId)) {
            return ResponseEntity.ok(new FlightDto(1L, "FL001", BigDecimal.valueOf(50),
                    LocalDateTime.of(2024, 11, 24, 8, 0), // 출발 시간
                    LocalDateTime.of(2024, 11, 24, 10, 0), // 도착 시간
                    "ICN", "NRT", Duration.ofHours(2), "Korean Air"));
        }
        return ResponseEntity.notFound().build();
    }


}
