package org.onewayticket.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onewayticket.flight.FlightProto;
import org.onewayticket.service.PriceAlertService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlightAddedConsumer {
    private final KafkaTemplate<String, FlightProto.Flight> kafkaTemplate;
    private final PriceAlertService priceAlertService;

    /**
     * consume : flight-added
     * produce : notification-event
     */
    @KafkaListener(topics = "flight-added", groupId = "flight-added-group")
    public void consume(FlightProto.Flight flight) {
        log.info("Consumed flight: {}", flight);
        priceAlertService.checkPriceAlerts(flight);
    }

}
