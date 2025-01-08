package org.onewayticket.generator;

import lombok.RequiredArgsConstructor;
import org.onewayticket.model.Flight;
import org.onewayticket.producer.FlightMessageProducer;

import java.util.List;

@RequiredArgsConstructor
public class FlightProcessor {
    private final FlightDataGenerator generator;
    private final FlightDataSaver saver;
    private final FlightMessageProducer producer;

    public void processFlights(int count) {
        List<Flight> flights = generator.generateRandomFlights(count);
        saver.saveFlights(flights);
        for (Flight flight : flights) {
            producer.produce(flight.getId(), flight);
        }
    }

}
