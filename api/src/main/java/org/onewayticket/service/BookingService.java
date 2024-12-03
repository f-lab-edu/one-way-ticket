package org.onewayticket.service;

import lombok.RequiredArgsConstructor;
import org.onewayticket.domain.Booking;
import org.onewayticket.domain.BookingDetail;
import org.onewayticket.domain.BookingResponse;
import org.onewayticket.domain.Flight;
import org.onewayticket.domain.Passenger;
import org.onewayticket.enums.BookingStatus;
import org.onewayticket.repository.BookingRepository;
import org.onewayticket.util.ReferenceCodeGenerator;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FlightService flightService;

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

    @Transactional
    public void cancelBooking(String id, String token) {
        Booking booking = bookingRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new IllegalArgumentException("해당 예약 정보가 없습니다."));
        String expectedToken = generateToken(booking.getReferenceCode(), booking.getBookingEmail());

        if (expectedToken.equals(token)) {
            booking.updateStatus(BookingStatus.CANCELLED);
            try {
                bookingRepository.save(booking);
            } catch (DataAccessException e) {
                throw new DataIntegrityViolationException("Booking 취소 중 예외가 발생했습니다: " + e.getMessage(), e);
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

    // 토큰 포함된 Response를 반환하기 위한 메서드
    public BookingResponse getBookingResponse(String referenceCode, String email){
        String token = generateToken(referenceCode, email);
        BookingDetail bookingDetail = getBookingDetails(referenceCode);
        return new BookingResponse(bookingDetail,token);
    }

    /**
     * 예약번호와 이메일 기반으로 해시 토큰 생성
     */
    public String generateToken(String reservationNumber, String email) {
        try {
            String data = reservationNumber + email;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating token", e);
        }
    }
}
