package org.onewayticket.service;

import lombok.RequiredArgsConstructor;
import org.onewayticket.domain.Flight;
import org.onewayticket.dto.FlightDto;
import org.onewayticket.repository.FlightRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightService {
    private final FlightRepository flightRepository;

    public FlightDto getFlightDetails(String flightId) {
        Flight flight = flightRepository.findById(Long.valueOf(flightId)).orElseThrow(() ->
                new NoSuchElementException("Flight not found with id: " + flightId));

        return this.convertToDto(flight);
    }

    public List<FlightDto> getCheapestFlights() {
        String origin = "ICN";
        List<Flight> flights = flightRepository.findByOriginOrderByAmountAsc(origin);
        return flights.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<FlightDto> searchFlights(String origin, String destination, String departureDate, String sort) {
        LocalDate parsedDate = LocalDate.parse(departureDate);

        List<Flight> flights = flightRepository.searchFlights(origin, destination, parsedDate);

        flights.sort(getComparatorForSort(sort));

        return flights.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private Comparator<Flight> getComparatorForSort(String sort) {
        return switch (sort) {
            case "price" -> Comparator.comparing(Flight::getAmount);
            case "arrivaltime" -> Comparator.comparing(Flight::getArrivalTime);
            case "flightduration" -> Comparator.comparing(f -> Duration.ofMinutes(f.getDurationInMinutes()));
            default -> throw new IllegalArgumentException("잘못된 정렬 방식입니다.");
        };
    }

    private FlightDto convertToDto(Flight flight) {
        return FlightDto.builder()
                .id(flight.getId())
                .flightNumber(flight.getFlightNumber())
                .amount(flight.getAmount())
                .departureTime(flight.getDepartureTime())
                .arrivalTime(flight.getArrivalTime())
                .origin(flight.getOrigin())
                .destination(flight.getDestination())
                .flightDuration(Duration.ofMinutes(flight.getDurationInMinutes()))
                .carrier(flight.getCarrier())
                .build();
    }
}
