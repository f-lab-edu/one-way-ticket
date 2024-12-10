package org.onewayticket.service;

import lombok.RequiredArgsConstructor;
import org.onewayticket.domain.Booking;
import org.onewayticket.domain.BookingDetail;
import org.onewayticket.domain.BookingResponse;
import org.onewayticket.domain.Flight;
import org.onewayticket.domain.Member;
import org.onewayticket.domain.Passenger;
import org.onewayticket.enums.BookingStatus;
import org.onewayticket.repository.BookingRepository;
import org.onewayticket.security.JwtUtil;
import org.onewayticket.util.ReferenceCodeGenerator;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.zip.CRC32;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FlightService flightService;
    private final MemberService memberService;
    private final JwtUtil jwtUtil;

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

    // 토큰 포함된 Response를 반환하기 위한 메서드
    public BookingResponse getBookingResponse(String referenceCode, String email) {
        String token = generateToken(referenceCode, email);
        BookingDetail bookingDetail = getBookingDetailsByReferenceCode(referenceCode);
        return new BookingResponse(bookingDetail, token);
    }

    public List<BookingDetail> getBookingDetailsList(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        String usernameFromToken = jwtUtil.getUsername(token);

        Member member = memberService.getMemberByUsername(usernameFromToken);
        List<Booking> bookings = bookingRepository.findAllByMemberId(member.getId());

        return bookings.stream().map(booking -> {
            Flight flight = flightService.getFlightDetails(booking.getFlightId().toString());
            return new BookingDetail(booking, flight);
        }).toList();
    }

    public BookingDetail getBookingDetailsForUser(Long bookingId, String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NoSuchElementException("예약 정보를 찾을 수 없습니다."));
        Flight flight = flightService.getFlightDetails(booking.getFlightId().toString());

        String usernameFromToken = jwtUtil.getUsername(token);
        String usernameInBooking = memberService.getMemberById(booking.getMemberId()).getUsername();
        if (!usernameInBooking.equals(usernameFromToken)) {
            throw new IllegalArgumentException("로그인한 사용자와 예약자가 일치하지 않습니다.");
        }

        return new BookingDetail(booking, flight);
    }

    @Transactional
    public void cancelBooking(String id, String token) {
        Booking booking = bookingRepository.findById(Long.valueOf(id)).orElseThrow(() -> new IllegalArgumentException("해당 예약 정보가 없습니다."));

        if (jwtUtil.isJwtToken(token)) {
            cancelBookingForMember(booking, token);
        } else {
            cancelBookingForGuest(booking, token);
        }
    }

    /**
     * 예약번호와 이메일 기반으로 해시 토큰 생성
     */
    private String generateToken(String reservationNumber, String email) {
        String data = reservationNumber + email;
        CRC32 crc32 = new CRC32();
        crc32.update(data.getBytes(StandardCharsets.UTF_8));
        return Long.toHexString(crc32.getValue());
    }


    private void cancelBookingForMember(Booking booking, String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("유효하지 않은 JWT 토큰입니다.");
        }

        String usernameFromToken = jwtUtil.getUsername(token);
        String usernameInBooking = memberService.getMemberById(booking.getMemberId()).getUsername();

        if (!usernameFromToken.equals(usernameInBooking)) {
            throw new IllegalArgumentException("로그인한 사용자와 예약자가 일치하지 않습니다.");
        }

        updateBookingStatusToCancelled(booking);
    }

    private void cancelBookingForGuest(Booking booking, String token) {
        String expectedToken = generateToken(booking.getReferenceCode(), booking.getBookingEmail());

        if (!expectedToken.equals(token)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
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
