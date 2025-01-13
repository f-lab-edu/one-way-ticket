package org.onewayticket.consumer;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onewayticket.domain.Flight;
import org.onewayticket.service.FlightService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlightAddedConsumer {
    private final FlightService flightService;

    @KafkaListener(topics = "flight-added", groupId = "flight-added-group", containerFactory = "kafkaListenerContainerFactory")
    public void onFlightAdded(Flight flight) {
        log.info("Consumed flight: {}", flight);
        flightService.handleFlightAdded(flight);
    }

}
