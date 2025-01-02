package org.onewayticket.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class Flight {
    private String flightNumber;
    private BigDecimal amount;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String origin;
    private String destination;
    private int durationInMinutes;
    private String carrier;
}