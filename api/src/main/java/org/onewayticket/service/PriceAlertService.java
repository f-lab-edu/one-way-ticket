package org.onewayticket.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onewayticket.domain.Flight;
import org.onewayticket.domain.PriceAlert;
import org.onewayticket.event.NotificationEvent;
import org.onewayticket.event.PriceAlertRequestEvent;
import org.onewayticket.repository.PriceAlertRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceAlertService {
    private final PriceAlertRepository priceAlertRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PriceAlert createPriceAlert(String username, String origin, String destination, BigDecimal targetAmount) {
        PriceAlert priceAlert = PriceAlert.builder().username(username).origin(origin)
                .destination(destination).targetAmount(targetAmount).build();
        priceAlertRepository.save(priceAlert);
        return priceAlert;
    }

    public PriceAlert removePriceAlert(String username, Long priceAlertId) {
        PriceAlert priceAlert = priceAlertRepository.findById(priceAlertId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림입니다."));
        if (!username.equals(priceAlert.getUsername())) {
            throw new IllegalArgumentException("로그인한 사용자와 해당 데이터의 사용자가 일치하지 않습니다.");
        }
        priceAlertRepository.deleteById(priceAlertId);
        return priceAlert;
    }

    public List<PriceAlert> getPriceAlertList(String origin, String destination, BigDecimal targetAmount) {
        return priceAlertRepository.findByOriginAndDestinationAndTargetAmountGreaterThanEqual(origin, destination, targetAmount);
    }

    public void handlePriceAlertRequest(PriceAlertRequestEvent requestEvent) {
        Flight flight = requestEvent.flight();

        log.info("Processing price alert request for flight: {}", flight);

        // DB에서 알림 대상 조회
        List<PriceAlert> matchingAlerts = priceAlertRepository.findByOriginAndDestinationAndTargetAmountGreaterThanEqual(
                flight.getOrigin(),
                flight.getDestination(),
                flight.getAmount()
        );

        // 매칭된 Alert에 대해 Notification 이벤트 발행
        for (PriceAlert alert : matchingAlerts) {
            NotificationEvent notificationEvent = NotificationEvent.of(alert, flight);
            kafkaTemplate.send("notification-event", notificationEvent.eventId(), notificationEvent);
            log.info("Sent notification-event: {}", notificationEvent);
        }
    }

}
