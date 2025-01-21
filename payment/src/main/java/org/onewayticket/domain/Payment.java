package org.onewayticket.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.onewayticket.enums.PaymentStatus;

import java.math.BigDecimal;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;

    private Long orderId;

    private String username;

    // 결제 상태
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    // 예: 결제 생성 시점, 최종 변경 시점 등
    // private LocalDateTime createdAt;
    // private LocalDateTime updatedAt;

    // 결제와 관련된 추가 정보들...
}
