package org.onewayticket.service;

import lombok.RequiredArgsConstructor;
import org.onewayticket.domain.Booking;
import org.onewayticket.domain.BookingDetail;
import org.onewayticket.domain.Flight;
import org.onewayticket.domain.Member;
import org.onewayticket.domain.Passenger;
import org.onewayticket.enums.BookingStatus;
import org.onewayticket.repository.BookingRepository;
import org.onewayticket.util.ReferenceCodeGenerator;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FlightService flightService;
    private final MemberService memberService;

    public Booking createBooking(String email, List<Passenger> passengers, String paymentKey, Long flightId) {
        Booking booking = Booking.builder().referenceCode(ReferenceCodeGenerator.generateReferenceCode()).bookingEmail(email).flightId(flightId).paymentKey(paymentKey).passengers(passengers).status(BookingStatus.COMPLETED).build();
        try {
            bookingRepository.save(booking);
        } catch (DataAccessException e) {
            throw new DataIntegrityViolationException("Booking 저장 중 예외가 발생했습니다: " + e.getMessage(), e);
        }
        return booking;
    }

    public BookingDetail getBookingDetailsByReferenceCode(String referenceCode) {
        // TODO: Index 추가 여부 고민
        Booking booking = bookingRepository.findByReferenceCode(referenceCode).orElseThrow(() -> new NoSuchElementException("예약 정보를 찾을 수 없습니다."));

        Flight flight = flightService.getFlightDetails(booking.getFlightId().toString());

        return new BookingDetail(booking, flight);
    }

    public List<BookingDetail> getBookingDetailsList(String username) {
        Member member = memberService.getMemberByUsername(username);
        List<Booking> bookings = bookingRepository.findAllByMemberId(member.getId());
        return bookings.stream().map(booking -> {
            Flight flight = flightService.getFlightDetails(booking.getFlightId().toString());
            return new BookingDetail(booking, flight);
        }).toList();
    }

    public BookingDetail getBookingDetailsForUser(Long bookingId, String username) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NoSuchElementException("예약 정보를 찾을 수 없습니다."));
        Flight flight = flightService.getFlightDetails(booking.getFlightId().toString());

        String usernameInBooking = booking.getBookingEmail();
        if (!usernameInBooking.equals(username)) {
            throw new IllegalArgumentException("로그인한 사용자와 예약자가 일치하지 않습니다.");
        }

        return new BookingDetail(booking, flight);
    }

    @Transactional
    public void cancelBooking(String id, String username) {
        Booking booking = bookingRepository.findById(Long.valueOf(id)).orElseThrow(() -> new IllegalArgumentException("해당 예약 정보가 없습니다."));

        String usernameInBooking = booking.getBookingEmail();

        if (!username.equals(usernameInBooking)) {
            throw new IllegalArgumentException("로그인한 사용자와 예약자가 일치하지 않습니다.");
        }

        updateBookingStatusToCancelled(booking);
    }

    private void updateBookingStatusToCancelled(Booking booking) {
        booking.updateStatus(BookingStatus.CANCELLED);

        try {
            bookingRepository.save(booking);
        } catch (DataAccessException e) {
            throw new DataIntegrityViolationException("Booking 취소 중 예외가 발생했습니다: " + e.getMessage(), e);
        }
    }
}
