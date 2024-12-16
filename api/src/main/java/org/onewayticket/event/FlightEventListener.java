package org.onewayticket.event;

import lombok.RequiredArgsConstructor;
import org.onewayticket.domain.Flight;
import org.onewayticket.domain.PriceAlert;
import org.onewayticket.service.PriceAlertService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FlightEventListener {
    private final PriceAlertService priceAlertService;

    @EventListener
    public void handleFlightAddedEvent(FlightAddedEvent event) {
        Flight flight = event.getFlight();
        List<PriceAlert> matchingAlerts = priceAlertService.getPriceAlertList(flight.getOrigin(), flight.getDestination(), flight.getAmount());
        for (PriceAlert alert : matchingAlerts) {
            sendNotifications(alert, flight);
        }
    }

    // TODO : Email, 푸시 알람으로 확장
    private void sendNotifications(PriceAlert alert, Flight flight) {
        String message = String.format(
                "사용자 [%s] 님이 설정한 조건에 맞는 항공권이 발견되었습니다!\n" +
                        "항공편 정보:\n" +
                        "출발지: %s, 도착지: %s, 가격: %s원, 출발 시간: %s",
                alert.getUsername(),                    // 사용자 이름
                flight.getOrigin(),                    // 출발지
                flight.getDestination(),               // 도착지
                flight.getAmount(),                    // 항공편 가격
                flight.getDepartureTime()              // 출발 시간
        );

        // 콘솔 출력
        System.out.println(message);
    }
}
