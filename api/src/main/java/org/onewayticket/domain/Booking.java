package org.onewayticket.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import org.onewayticket.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String referenceCode; // 사용자 조회용 예약 번호

    private Long userId;

    private Long flightId;

    private Long paymentId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "booking_id") // 외래 키를 Booking 테이블에 추가
    private List<Passenger> passengers = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private LocalDateTime createdAt; // 예약 시간

}
