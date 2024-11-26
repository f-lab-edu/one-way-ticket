package org.onewayticket.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String flightNumber;

    private BigDecimal amount;

    private LocalDateTime departureTime;

    private LocalDateTime arrivalTime;

    private String origin;

    private String destination;

    private int durationInMinutes;

    private String carrier; // 항공사
}
