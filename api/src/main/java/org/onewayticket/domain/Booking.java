package org.onewayticket.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.onewayticket.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private String referenceCode; // 사용자 조회용 예약 번호

    private String bookingEmail;

    private Long flightId;

    private String paymentKey;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "booking_id")
    @Builder.Default
    private List<Passenger> passengers = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private LocalDateTime createdAt; // 예약 시간

    public void updateStatus(BookingStatus bookingStatus) {
        this.status = bookingStatus;
    }

}
