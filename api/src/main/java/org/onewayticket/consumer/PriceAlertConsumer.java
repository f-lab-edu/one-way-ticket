package org.onewayticket.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onewayticket.event.PriceAlertRequestEvent;
import org.onewayticket.service.PriceAlertService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceAlertConsumer {

    private final PriceAlertService priceAlertService;

    @KafkaListener(topics = "price-alert-requested", groupId = "price-alert-group")
    public void onPriceAlertRequested(PriceAlertRequestEvent requestEvent) {
        log.info("Consumed message from price-alert-requested: {}", requestEvent);
        priceAlertService.handlePriceAlertRequest(requestEvent);
    }
}
