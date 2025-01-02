package org.onewayticket.generator;

import lombok.RequiredArgsConstructor;
import org.onewayticket.model.Flight;
import org.onewayticket.producer.FlightMessageProducer;

import java.util.List;

@RequiredArgsConstructor
public class FlightProcessor {
//    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final FlightDataGenerator generator;
    private final FlightDataSaver saver;
    private final FlightMessageProducer producer;

    public void processFlights(int count) {
        List<Flight> flights = generator.generateRandomFlights(count);
        saver.saveFlights(flights);
        for (Flight flight : flights) {
            String flightJson = flightToJson(flight);
            producer.produce(flight.getFlightId(), flightJson);
        }
    }

    private String flightToJson(Flight flight) {
        return String.format(
                "{\"flightId\":\"%s\",\"origin\":\"%s\",\"destination\":\"%s\",\"amount\":%s,\"departureTime\":\"%s\"}",
                flight.getFlightNumber(),
                flight.getOrigin(),
                flight.getDestination(),
                flight.getAmount(),
                flight.getDepartureTime()
        );
    }

}
