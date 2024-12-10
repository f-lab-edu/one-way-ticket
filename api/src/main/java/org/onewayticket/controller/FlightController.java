package org.onewayticket.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.onewayticket.domain.Flight;
import org.onewayticket.dto.FlightDto;
import org.onewayticket.service.FlightService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/flights")
public class FlightController {
    private final FlightService flightService;

    @GetMapping("/cheapest")
    public ResponseEntity<List<FlightDto>> getCheapestFlights() {
        List<Flight> flights = flightService.getCheapestFlights();

        return ResponseEntity.ok(FlightDto.from(flights));
    }

    @GetMapping("/search")
    public ResponseEntity<List<FlightDto>> searchFlights(
            @RequestParam("origin") @NotNull String origin,
            @RequestParam("destination") @NotNull String destination,
            @RequestParam("departureDate") @NotNull String departureDate,
            @RequestParam(value = "sort", defaultValue = "price") @NotNull String sort) {
        List<Flight> flightList = flightService.searchFlights(origin, destination, departureDate, sort);
        return ResponseEntity.ok(FlightDto.from(flightList));
    }


    @GetMapping("/{flightId}")
    public ResponseEntity<?> getFlightDetails(@PathVariable @NotNull String flightId) {
        Flight flight = flightService.getFlightDetails(flightId);
        return ResponseEntity.ok(FlightDto.from(flight));
    }
}
