package org.onewayticket.consumer;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.onewayticket.domain.Flight;
import org.onewayticket.domain.PriceAlert;
import org.onewayticket.service.PriceAlertService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightConsumer {

    private final PriceAlertService priceAlertService;

    @KafkaListener(topics = "flight-added", groupId = "flight-alert-group")
    public void consumeFlightEvent(String flightData) {
        System.out.println("Consumed Flight Data: " + flightData);

        Flight flight = parseFlightData(flightData);

        List<PriceAlert> matchingAlerts = priceAlertService.getPriceAlertList(
                flight.getOrigin(), flight.getDestination(), flight.getAmount());

        for (PriceAlert alert : matchingAlerts) {
            sendNotifications(alert, flight);
        }
    }

    private Flight parseFlightData(String flightData) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(flightData, Flight.class);
        } catch (Exception e) {
            throw new RuntimeException("파싱 실패 " + flightData, e);
        }
    }

    private void sendNotifications(PriceAlert alert, Flight flight) {
        String message = String.format(
                "사용자 [%s] 님이 설정한 조건에 맞는 항공권이 발견되었습니다!\n" +
                        "항공편 정보:\n" +
                        "출발지: %s, 도착지: %s, 가격: %s원, 출발 시간: %s",
                alert.getUsername(),
                flight.getOrigin(),
                flight.getDestination(),
                flight.getAmount(),
                flight.getDepartureTime()
        );

        // 콘솔 출력
        System.out.println(message);

        // TODO: Email, 푸시 알림 로직 추가
    }
}