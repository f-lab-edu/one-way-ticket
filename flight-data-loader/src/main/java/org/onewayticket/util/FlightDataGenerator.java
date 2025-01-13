package org.onewayticket.util;

import org.onewayticket.model.Flight;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
public class FlightDataGenerator {
    private final Random random = new Random();

    public List<Flight> generateRandomFlights(int count) {
        List<Flight> flights = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            flights.add(Flight.builder()
                    .id(UUID.randomUUID().toString())
                    .flightNumber(UUID.randomUUID().toString().substring(0, 6).toUpperCase())
                    .amount(BigDecimal.valueOf(50 + random.nextDouble() * 500))
                    .departureTime(LocalDateTime.now().plusHours(random.nextInt(24)))
                    .arrivalTime(LocalDateTime.now().plusHours(25 + random.nextInt(24)))
                    .origin(getRandomAirportCode())
                    .destination(getRandomAirportCode())
                    .durationInMinutes(60 + random.nextInt(600))
                    .carrier(getRandomCarrier())
                    .build());
        }
        return flights;
    }

    private String getRandomAirportCode() {
        String[] airportCodes = {"ICN", "JFK", "LAX", "CDG", "NRT"};
        return airportCodes[random.nextInt(airportCodes.length)];
    }

    private String getRandomCarrier() {
        String[] carriers = {"Korean Air", "Delta", "American Airlines", "Air France", "ANA"};
        return carriers[random.nextInt(carriers.length)];
    }
}
