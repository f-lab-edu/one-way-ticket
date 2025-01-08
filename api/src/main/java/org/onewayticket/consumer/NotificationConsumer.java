package org.onewayticket.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onewayticket.event.NotificationEvent;
import org.onewayticket.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationConsumer {
    private final NotificationService notificationService;

    @KafkaListener(topics = "notification-event", groupId = "notification-group")
    public void onNotificationEvent(NotificationEvent notificationEvent) {
        log.info("Consumed message from notification-event: {}", notificationEvent);
        notificationService.sendNotification(notificationEvent);

    }
}
