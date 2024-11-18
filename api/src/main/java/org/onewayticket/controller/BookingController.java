package org.onewayticket.controller;

import lombok.RequiredArgsConstructor;
import org.onewayticket.dto.BookingDetailsDto;
import org.onewayticket.dto.BookingDto;
import org.onewayticket.dto.BookingRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookings")
public class BookingController {

    @PostMapping
    public ResponseEntity<String> createBooking(@RequestBody BookingRequestDto bookingRequestInfo) {
        if (bookingRequestInfo.paymentId() == null || bookingRequestInfo.paymentId().isBlank()) {
            return ResponseEntity.badRequest().body("Payment information is missing");
        }
        return ResponseEntity.ok("Booking created");
    }

    // 예약 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<BookingDetailsDto> getBookingDetails(@PathVariable String id) {
        BookingDetailsDto bookingDetails = new BookingDetailsDto(
                id, "John Doe", "john.doe@example.com", "123456789",
                "FL123", "ICN", "NRT", LocalDate.of(2023, 12, 1), LocalDate.of(2023, 12, 1),
                "Jane Doe", 25, "Female", "AB123456", "Korean", "12A", "Economy",
                BigDecimal.valueOf(500), "Confirmed"
        );
        return ResponseEntity.ok(bookingDetails);
    }

    // 예약 취소
    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelBooking(@PathVariable String id, @RequestHeader("Authorization") String authHeader) {
        // 토큰 검증
        if (!authHeader.equals("Bearer VALID_TOKEN")) {
            return ResponseEntity.status(403).body("Unauthorized user");
        }

        return ResponseEntity.ok("Booking canceled");
    }


    @GetMapping("/my")
    public ResponseEntity<List<BookingDto>> getMyBookings(
            @RequestParam("bookingId") String bookingId,
            @RequestParam("name") String name,
            @RequestParam("birthDate") String birthDate
    ) {
        // 더미 데이터
        List<BookingDto> bookings = List.of(
                new BookingDto("BK001", "John Doe", "john.doe@example.com", "123456789"),
                new BookingDto("BK002", "Jane Smith", "jane.smith@example.com", "987654321")
        );

        // 사용자 정보로 필터링
        List<BookingDto> filteredBookings = bookings.stream()
                .filter(booking -> booking.bookingId().equals(bookingId) && booking.reservationName().equalsIgnoreCase(name))
                .toList();

        // 조건에 따라 응답 처리
        if (filteredBookings.isEmpty()) {
            return ResponseEntity.status(404).build(); // 404 반환
        }

        return ResponseEntity.ok(filteredBookings);
    }


}
