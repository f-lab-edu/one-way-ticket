package org.onewayticket.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.onewayticket.domain.Booking;
import org.onewayticket.domain.BookingDetail;
import org.onewayticket.dto.BookingDetailsDto;
import org.onewayticket.dto.BookingRequestDto;
import org.onewayticket.dto.PassengerDto;
import org.onewayticket.security.JwtUtil;
import org.onewayticket.service.BookingService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final JwtUtil jwtUtil;

    // 예약 생성
    @PostMapping
    public ResponseEntity<Booking> createBooking(@Valid @RequestBody BookingRequestDto bookingRequestInfo,
                                                 @NotNull @RequestParam String flightId) {
        Booking booking = bookingService.createBooking(bookingRequestInfo.bookingEmail(), PassengerDto.from(bookingRequestInfo.passengers()), bookingRequestInfo.paymentKey(), Long.parseLong(flightId));
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    @GetMapping
    public ResponseEntity<?> getBookingDetailsList(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        List<BookingDetailsDto> bookingDetailsDtoList = BookingDetailsDto.fromList(bookingService.getBookingDetailsList(username));
        return ResponseEntity.ok(bookingDetailsDtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingDetailsForMember(@PathVariable @NotNull Long id, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        BookingDetailsDto bookingDetailsDto = BookingDetailsDto.from(bookingService.getBookingDetailsForUser(id, username));
        return ResponseEntity.ok(bookingDetailsDto);
    }

    @GetMapping("/guest")
    public ResponseEntity<BookingDetailsDto> getBookingDetailsByReference(
            @RequestParam("referenceCode") @NotNull String referenceCode,
            @RequestParam("bookingEmail") @NotNull String bookingEmail) {
        BookingDetail bookingDetail = bookingService.getBookingDetailsByReferenceCode(referenceCode);
        String token = jwtUtil.generateToken(bookingEmail, 15 * 60 * 1000);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        return ResponseEntity.ok().headers(headers).body(BookingDetailsDto.from(bookingDetail));
    }

    // 예약 취소(회원, 비회원 통합)
    @PostMapping("/{id}")
    public ResponseEntity<String> cancelBooking(
            @PathVariable @NotNull String id,
            HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        bookingService.cancelBooking(id, username);
        return ResponseEntity.ok("해당 예약이 취소되었습니다.");
    }

}
