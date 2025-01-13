package org.onewayticket.service;

import lombok.RequiredArgsConstructor;
import org.onewayticket.model.Flight;
import org.onewayticket.producer.FlightEventPublisher;
import org.onewayticket.util.FlightDataGenerator;
import org.onewayticket.util.FlightDataSaver;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class FlightProcessor {
    private final FlightDataGenerator generator;
    private final FlightDataSaver saver;
    private final FlightEventPublisher eventPublisher;

    public void processFlights(int count) {
        List<Flight> flights = generator.generateRandomFlights(count);
        saver.saveFlights(flights);
        flights.forEach(eventPublisher::publish);
    }
}