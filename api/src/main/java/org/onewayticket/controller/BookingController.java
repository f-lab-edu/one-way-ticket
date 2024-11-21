package org.onewayticket.controller;

import lombok.RequiredArgsConstructor;
import org.onewayticket.dto.BookingDetailsDto;
import org.onewayticket.dto.BookingRequestDto;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookings")
public class BookingController {

    // 예약 생성
    @PostMapping
    public ResponseEntity<BookingDetailsDto> createBooking(@RequestBody BookingRequestDto bookingRequestInfo) {
        if (bookingRequestInfo.paymentId() == null || bookingRequestInfo.paymentId().isBlank() || !bookingRequestInfo.paymentId().equals("Confirmed")) {
            return ResponseEntity.status(400).build();
        }

        return ResponseEntity.ok(new BookingDetailsDto("A1234", bookingRequestInfo.bookingName(), bookingRequestInfo.bookingEmail(), bookingRequestInfo.bookingPhoneNumber(),
                bookingRequestInfo.flightId(), "ICN", "NRT", LocalDate.of(2023, 12, 1), LocalDate.of(2023, 12, 1), "Jane Doe",
                LocalDate.parse(bookingRequestInfo.birthDate()), 25, "Female", "AB123456", "Korean", "12A", "Economy", BigDecimal.valueOf(500), "Confirmed"));
    }

    @GetMapping
    public ResponseEntity<BookingDetailsDto> getBookingDetails(
            @RequestParam("bookingId") String bookingId,
            @RequestParam("name") String name,
            @RequestParam("birthDate") String birthDate) {

        // 날짜 유효성 검증
        if (!isValidDate(birthDate)) {
            return ResponseEntity.badRequest().body(null);
        }

        // 미리 정의된 예약 정보
        BookingDetailsDto bookingDetails = new BookingDetailsDto(
                "B1234", "John Doe", "john.doe@example.com", "123456789",
                "FL123", "ICN", "NRT", LocalDate.of(2023, 12, 1), LocalDate.of(2023, 12, 1),
                name, LocalDate.parse(birthDate), 25, "Female", "AB123456", "Korean", "12A", "Economy",
                BigDecimal.valueOf(500), "Confirmed");

        // bookingId가 일치하지 않을 경우 예외 처리
        if (!bookingDetails.bookingId().equals(bookingId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(bookingDetails);
    }


    // 예약 취소
    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelBooking(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        // 토큰 검증
        if (authHeader == null || !authHeader.equals("Bearer VALID_TOKEN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 메시지 없이 401 반환
        }

        // 200 OK와 함께 예약 ID 반환
        return ResponseEntity.ok("Booking with ID " + id + " has been canceled successfully.");
    }


    // 오늘 이후 날짜이거나 입력 값이 맞는지 확인
    private boolean isValidDate(String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date); // 기본 포맷 yyyy-MM-dd
            return !parsedDate.isAfter(LocalDate.now()); // 오늘 이후 날짜인지 확인
        } catch (DateTimeParseException e) {
            return false; // 형식이 잘못된 경우 false 반환
        }
    }


}
