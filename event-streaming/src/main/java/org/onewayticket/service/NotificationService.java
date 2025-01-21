package org.onewayticket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {
        // TODO: 외부 모듈로 추출 및 사용자 전체 푸시 알림 로직 구현
    public void sendNotificationToUser(String username, String message) {
        log.info("Sending push notification to user [{}]: {}", username, message);
    }
}

