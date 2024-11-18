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
                new FlightDto("FL001", BigDecimal.valueOf(50), LocalDateTime.now(), "ICN", "NRT",
                        LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(4), Duration.ofHours(2)),
                new FlightDto("FL002", BigDecimal.valueOf(70), LocalDateTime.now(), "ICN", "HND",
                        LocalDateTime.now().plusHours(3), LocalDateTime.now().plusHours(5), Duration.ofHours(2)),
                new FlightDto("FL003", BigDecimal.valueOf(80), LocalDateTime.now(), "ICN", "KIX",
                        LocalDateTime.now().plusHours(4), LocalDateTime.now().plusHours(6), Duration.ofHours(2)),
                new FlightDto("FL004", BigDecimal.valueOf(100), LocalDateTime.now(), "ICN", "LAX",
                        LocalDateTime.now().plusHours(5), LocalDateTime.now().plusHours(16), Duration.ofHours(11)),
                new FlightDto("FL005", BigDecimal.valueOf(120), LocalDateTime.now(), "ICN", "JFK",
                        LocalDateTime.now().plusHours(6), LocalDateTime.now().plusHours(20), Duration.ofHours(14)),
                new FlightDto("FL006", BigDecimal.valueOf(150), LocalDateTime.now(), "ICN", "CDG",
                        LocalDateTime.now().plusHours(7), LocalDateTime.now().plusHours(19), Duration.ofHours(12)),
                new FlightDto("FL007", BigDecimal.valueOf(200), LocalDateTime.now(), "ICN", "SYD",
                        LocalDateTime.now().plusHours(8), LocalDateTime.now().plusHours(18), Duration.ofHours(10)),
                new FlightDto("FL008", BigDecimal.valueOf(300), LocalDateTime.now(), "ICN", "FRA",
                        LocalDateTime.now().plusHours(9), LocalDateTime.now().plusHours(22), Duration.ofHours(13)),
                new FlightDto("FL009", BigDecimal.valueOf(350), LocalDateTime.now(), "ICN", "SIN",
                        LocalDateTime.now().plusHours(10), LocalDateTime.now().plusHours(16), Duration.ofHours(6))
        );

        // 가격 기준으로 정렬 후 상위 9개 반환
        return ResponseEntity.ok(flights.stream()
                .sorted((f1, f2) -> f1.price().compareTo(f2.price()))
                .limit(9)
                .toList());
    }

    @GetMapping("/search")
    public ResponseEntity<List<FlightDto>> searchFlights(
            @RequestParam("departure") String departure,
            @RequestParam("destination") String destination,
            @RequestParam("departureDate") String departureDate,
            @RequestParam("numberOfPassengers") Integer numberOfPassengers) {
        // 출발지와 목적지가 같은 경우
        if (departure.equalsIgnoreCase(destination)) {
            return ResponseEntity.badRequest().body(List.of());
        }

        // 예제 데이터 반환
        return ResponseEntity.ok(List.of(
                new FlightDto("FL003", BigDecimal.valueOf(120), LocalDateTime.now(), departure, destination,
                        LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(4), Duration.ofHours(2))
        ));
    }


    @GetMapping("/{flightId}")
    public ResponseEntity<FlightDto> getFlightDetails(@PathVariable String flightId) {
        if (flightId.length() > 50) { // 예제: ID가 50자를 초과하면 오류 반환
            return ResponseEntity.badRequest().build();
        }

        if ("FL001".equals(flightId)) {
            return ResponseEntity.ok(new FlightDto(
                    "FL001", BigDecimal.valueOf(50), LocalDateTime.now(), "ICN", "NRT",
                    LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(4), Duration.ofHours(2)
            ));
        }
        return ResponseEntity.notFound().build();
    }


}
