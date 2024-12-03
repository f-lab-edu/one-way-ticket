package org.onewayticket.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.onewayticket.domain.Booking;
import org.onewayticket.domain.BookingResponse;
import org.onewayticket.dto.BookingDetailsDto;
import org.onewayticket.dto.BookingRequestDto;
import org.onewayticket.dto.BookingResponseDto;
import org.onewayticket.dto.PassengerDto;
import org.onewayticket.security.TokenProvider;
import org.onewayticket.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final TokenProvider tokenProvider;

    // 예약 생성
    @PostMapping
    public ResponseEntity<Booking> createBooking(@Valid @RequestBody BookingRequestDto bookingRequestInfo,
                                                 @NotNull @RequestParam String flightId) {
        Booking booking = bookingService.createBooking(bookingRequestInfo.bookingEmail(), PassengerDto.from(bookingRequestInfo.passengers()), bookingRequestInfo.paymentKey(), Long.parseLong(flightId));
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);

    }

    @GetMapping
    public ResponseEntity<BookingResponseDto> getBookingDetailsDto(
            @RequestParam("referenceCode") @NotNull String referenceCode,
            @RequestParam("bookingEmail") @NotNull String bookingEmail) {

        BookingResponse bookingResponse = bookingService.getBookingResponse(referenceCode, bookingEmail);
        // record 인스턴스 생성
        BookingResponseDto responseDto = BookingResponseDto.from(bookingResponse);

        // DTO 반환
        return ResponseEntity.ok(responseDto);

    }

    // 예약 취소
    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelBooking(
            @PathVariable @NotNull String id,
            @RequestHeader(value = "Authorization", required = true) @NotNull String token) {
        bookingService.cancelBooking(id, token);
        return ResponseEntity.ok("Booking with ID " + id + " has been canceled successfully.");

    }


}
