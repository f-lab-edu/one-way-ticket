package org.onewayticket.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onewayticket.flight.FlightProto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PriceAlertService {

    private final JdbcTemplate jdbcTemplate;
    private final NotificationService notificationService;

    public void checkPriceAlerts(FlightProto.Flight flight) {
        String sql = "SELECT username, target_amount FROM price_alert WHERE origin = ? AND destination = ? AND target_amount >= ?";
        List<Map<String, Object>> alerts = jdbcTemplate.queryForList(sql, flight.getOrigin(), flight.getDestination(), flight.getAmount());

        for (Map<String, Object> alert : alerts) {
            String username = (String) alert.get("username");
            BigDecimal targetAmount = (BigDecimal) alert.get("target_amount");

            String message = String.format("New flight to %s under $%.2f!", flight.getDestination(), targetAmount);
            notificationService.sendNotificationToUser(username, message);
            log.info("Price alert notification sent to user [{}]: {}", username, message);
        }
    }
}