package org.onewayticket.runner;

import lombok.RequiredArgsConstructor;
import org.onewayticket.service.FlightProcessor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInjectorRunner implements CommandLineRunner {

    private final FlightProcessor processor;

    @Override
    public void run(String... args) {
        try {
            processor.processFlights(1);
        } catch (Exception e) {
            System.err.println("Error processing flights: " + e.getMessage());
        }
    }
}
