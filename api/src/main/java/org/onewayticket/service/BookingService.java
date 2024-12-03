package org.onewayticket.service;

import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.onewayticket.domain.Booking;
import org.onewayticket.domain.BookingDetail;
import org.onewayticket.domain.BookingResponse;
import org.onewayticket.domain.Flight;
import org.onewayticket.domain.Passenger;
import org.onewayticket.dto.BookingResponseDto;
import org.onewayticket.enums.BookingStatus;
import org.onewayticket.repository.BookingRepository;
import org.onewayticket.security.TokenProvider;
import org.onewayticket.util.ReferenceCodeGenerator;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FlightService flightService;
    private final TokenProvider tokenProvider;

    public Booking createBooking(String email, List<Passenger> passengers, String paymentKey, Long flightId) {
        Booking booking = Booking.builder().referenceCode(ReferenceCodeGenerator.generateReferenceCode())
                .bookingEmail(email).flightId(flightId).paymentKey(paymentKey)
                .passengers(passengers).status(BookingStatus.COMPLETED).build();
        try {
            bookingRepository.save(booking);
        } catch (DataAccessException e) {
            throw new DataIntegrityViolationException("Booking 저장 중 예외가 발생했습니다: " + e.getMessage(), e);
        }
        return booking;
    }

    public void cancelBooking(String id, String token) {
        Booking booking = bookingRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new IllegalArgumentException("해당 예약 정보가 없습니다."));
        String expectedToken = tokenProvider.generateToken(booking.getReferenceCode(), booking.getBookingEmail());

        if (expectedToken.equals(token)) {
            try {
                bookingRepository.delete(booking);
            } catch (DataAccessException e) {
                throw new DataIntegrityViolationException("Booking 삭제 중 예외가 발생했습니다: " + e.getMessage(), e);
            }
        } else {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

    }
    public BookingDetail getBookingDetails(String referenceCode) {

        // TODO: reference Code 형식 정한 후 검증
        // TODO: Index 추가 여부 고민
        Booking booking = bookingRepository.findByReferenceCode(referenceCode)
                .orElseThrow(() -> new NoSuchElementException("예약 정보를 찾을 수 없습니다."));

        Flight flight = flightService.getFlightDetails(booking.getFlightId().toString());

        return new BookingDetail(booking, flight);
    }

    public BookingResponse getBookingResponse(String referenceCode, String email){
        String token = tokenProvider.generateToken(referenceCode, email);
        BookingDetail bookingDetail = getBookingDetails(referenceCode);
        return new BookingResponse(bookingDetail,token);
    }
}
