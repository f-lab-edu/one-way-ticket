package org.onewayticket.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Flight {

    @Id
    @Column(nullable = false, unique = true)
    private String id;

    private String flightNumber;

    private BigDecimal amount;

    private LocalDateTime departureTime;

    private LocalDateTime arrivalTime;

    private String origin;

    private String destination;

    private int durationInMinutes;

    private String carrier; // 항공사
}
