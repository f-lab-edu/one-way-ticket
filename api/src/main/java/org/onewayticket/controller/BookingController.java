package org.onewayticket.controller;

import lombok.RequiredArgsConstructor;
import org.onewayticket.dto.BookingDetailsDto;
import org.onewayticket.dto.BookingRequestDto;
import org.onewayticket.dto.BookingResponseDto;
import org.onewayticket.security.AuthService;
import org.onewayticket.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final AuthService authService;

    // 예약 생성
    @PostMapping
    public ResponseEntity<BookingDetailsDto> createBooking(@RequestBody BookingRequestDto bookingRequestInfo,
                                                           @PathVariable String flightId) {

        // 결제 정보 검증
        if (bookingRequestInfo.paymentKey() == null || bookingRequestInfo.paymentKey().isBlank()) {
            return ResponseEntity.status(400).build();
        }

        bookingService.createBooking(bookingRequestInfo, flightId);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @GetMapping
    public ResponseEntity<BookingResponseDto> getBookingDetails(
            @RequestParam("referenceCode") String referenceCode,
            @RequestParam("bookingEmail") String bookingEmail) {


        BookingDetailsDto bookingDetailsDto = bookingService.getBookingDetails(referenceCode);

        // bookingId가 일치하지 않을 경우 예외 처리
        if (!bookingDetailsDto.referenceCode().equals(referenceCode)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        String token = authService.generateToken(referenceCode, bookingEmail);

        // record 인스턴스 생성
        BookingResponseDto responseDto = new BookingResponseDto(token, bookingDetailsDto);

        // DTO 반환
        return ResponseEntity.ok(responseDto);

    }

    // 예약 취소
    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelBooking(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = true) String token) {

        try {
            bookingService.cancelBooking(id, token);
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
        return ResponseEntity.ok("Booking with ID " + id + " has been canceled successfully.");

    }


}
