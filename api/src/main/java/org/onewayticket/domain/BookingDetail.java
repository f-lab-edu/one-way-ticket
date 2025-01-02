package org.onewayticket.domain;

import lombok.Getter;

import java.util.List;

@Getter
public class BookingDetail {
    private Long bookingId;

    private String referenceCode; // 사용자 조회용 예약 번호

    private String bookingEmail;

    private Flight flight;

    private String paymentKey;

    private List<Passenger> passengers;

    public BookingDetail(Booking booking, Flight flight) {
        this.bookingId = booking.getId();
        this.referenceCode = booking.getReferenceCode();
        this.bookingEmail = booking.getBookingEmail();
        this.flight = flight;
        this.paymentKey = booking.getPaymentKey();
        this.passengers = booking.getPassengers();
    }
}
