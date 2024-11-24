package org.onewayticket.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TossPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column(nullable = false, unique = true)
    private String tossPaymentKey;

    // 토스 내부 별도 orderId
    @Column(nullable = false)
    private String tossOrderId;

    private long totalAmount;

//    private Booking booking;

//    @Enumerated(value = EnumType.STRING)
//    @Column(nullable = false)
//    private TossPaymentMethod tossPaymentMethod;
//
//    @Enumerated(value = EnumType.STRING)
//    @Column(nullable = false)
//    private TossPaymentStatus tossPaymentStatus;

    @Column(nullable = false)
    private LocalDateTime requestedAt;


}