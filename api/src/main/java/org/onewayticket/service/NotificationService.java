package org.onewayticket.service;

import lombok.extern.slf4j.Slf4j;
import org.onewayticket.event.NotificationEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    @Async
    public void sendNotification(NotificationEvent notificationEvent) {
        log.info("Sending notification {}", notificationEvent.eventId());

        // 알림 발송 시뮬레이션
        callSMSApi();

        log.info("Notification sent for flight: {}", notificationEvent.flight().getId());
    }

    private void callSMSApi() {
        try {
            // api 호출 대신 sleep
            long delay = 100 + (long) (Math.random() * 100);
            Thread.sleep(delay);
            log.info("Simulated delay: {} ms", delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Simulation interrupted", e);
        }
    }
}
