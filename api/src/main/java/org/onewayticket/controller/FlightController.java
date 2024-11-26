package org.onewayticket.controller;

import lombok.RequiredArgsConstructor;
import org.onewayticket.dto.FlightDto;
import org.onewayticket.service.FlightService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/flights")
public class FlightController {
    private final FlightService flightService;

    @GetMapping("/cheapest")
    public ResponseEntity<List<FlightDto>> getCheapestFlights() {
        List<FlightDto> flightDtos = flightService.getCheapestFlights();
        return ResponseEntity.ok(flightDtos);
    }

    @GetMapping("/search")
    public ResponseEntity<List<FlightDto>> searchFlights(
            @RequestParam("origin") String origin,
            @RequestParam("destination") String destination,
            @RequestParam("departureDate") String departureDate,
            @RequestParam(value = "sort", defaultValue = "price") String sort) {

        List<FlightDto> flightDtoList = flightService.searchFlights(origin, destination, departureDate, sort);
        return ResponseEntity.ok(flightDtoList);
    }


    @GetMapping("/{flightId}")
    public ResponseEntity<?> getFlightDetails(@PathVariable String flightId) {

        // 유효성 검사
        if (flightId.length() > 50 || flightId == null || flightId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            FlightDto flightDto = flightService.getFlightDetails(flightId);
            return ResponseEntity.ok(flightDto);
        } catch (NoSuchElementException e) {
            // flightId에 해당하는 항공편이 없을 경우 404 Not Found 반환
            return ResponseEntity.notFound().build();
        }
    }
}



