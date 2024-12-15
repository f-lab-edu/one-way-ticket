package org.onewayticket.service;

import lombok.RequiredArgsConstructor;
import org.onewayticket.domain.Flight;
import org.onewayticket.repository.FlightRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class FlightService {
    private final FlightRepository flightRepository;

    public Flight getFlightDetails(String flightId) {
        if (flightId.isBlank()) throw new IllegalArgumentException("flightId 값이 전달되지 않았습니다.");
        return flightRepository.findById(Long.valueOf(flightId)).orElseThrow(() ->
                new NoSuchElementException("해당 flightId가 존재하지 않습니다. " + flightId));
    }

    public List<Flight> getCheapestFlights() {
        String origin = "ICN";
        return flightRepository.findByOriginOrderByAmountAsc(origin).orElseThrow(() ->
                new NoSuchElementException("해당 조건에 맞는 항공권이 존재하지 않습니다."));
    }

    public List<Flight> searchFlights(String origin, String destination, String departureDate, String sort) {
        LocalDate parsedDate = parseDate(departureDate);
        LocalDateTime startOfDay = parsedDate.atStartOfDay();
        LocalDateTime endOfDay = parsedDate.atTime(LocalTime.MAX);
        validateSortOption(sort); // 정렬 옵션 검증
        List<Flight> flights = flightRepository.findByOriginAndDestinationAndDepartureTimeBetween(
                origin, destination, startOfDay, endOfDay
        ).orElseThrow(
                () -> new NoSuchElementException("해당 조건에 맞는 항공권이 존재하지 않습니다."));
        flights.sort(getComparatorForSort(sort));
        return flights;
    }

    private Comparator<Flight> getComparatorForSort(String sort) {
        return switch (sort) {
            case "price" -> Comparator.comparing(Flight::getAmount);
            case "arrivalTime" -> Comparator.comparing(Flight::getArrivalTime);
            case "flightDuration" -> Comparator.comparing(f -> Duration.ofMinutes(f.getDurationInMinutes()));
            default -> throw new IllegalArgumentException("잘못된 정렬 방식입니다.");
        };
    }

    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("입력한 날짜 포맷을 확인해주세요.: " + date, e);
        }
    }

    private void validateSortOption(String sort) {
        List<String> validSortOptions = List.of("price", "arrivalTime", "flightDuration");
        if (!validSortOptions.contains(sort)) {
            throw new IllegalArgumentException("잘못된 정렬 방식입니다.");
        }
    }
}
