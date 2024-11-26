package org.onewayticket.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.onewayticket.dto.BookingRequestDto;
import org.onewayticket.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String referenceCode; // 사용자 조회용 예약 번호

    private String bookingEmail;

    private Long flightId;

    private String paymentKey;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "booking_id")
    @Builder.Default
    private List<Passenger> passengers = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(50)")
    private BookingStatus status;


    private LocalDateTime createdAt; // 예약 시간

    public static Booking from(BookingRequestDto dto, Long flightId) {
        return Booking.builder()
                .referenceCode(generateReferenceCode())
                .bookingEmail(dto.bookingEmail())
                .flightId(flightId)
                .paymentKey(dto.paymentKey())
                .status(BookingStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .passengers(dto.passengers().stream()
                        .map(Passenger::from)
                        .toList())
                .build();
    }

    private static String generateReferenceCode() {
        return UUID.randomUUID().toString().substring(0, 8); // 앞 8자리만 사용
    }

}
