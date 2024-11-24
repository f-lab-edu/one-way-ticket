package org.onewayticket.controller;

import lombok.RequiredArgsConstructor;
import org.onewayticket.dto.BookingDetailsDto;
import org.onewayticket.dto.BookingRequestDto;
import org.onewayticket.dto.FlightDto;
import org.onewayticket.dto.PassengerDto;
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
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookings")
public class BookingController {

    // 예약 생성
    @PostMapping
    public ResponseEntity<BookingDetailsDto> createBooking(@RequestBody BookingRequestDto bookingRequestInfo) {

        // 결제 정보 검증
        if (bookingRequestInfo.paymentId() == null || bookingRequestInfo.paymentId().isBlank() || !bookingRequestInfo.paymentId().equals("Confirmed")) {
            return ResponseEntity.status(400).build();
        }

        // 항공편 정보 생성
        FlightDto flightDto = new FlightDto(
                123L, "FL123", BigDecimal.valueOf(500),
                LocalDateTime.of(2023, 12, 1, 8, 0),
                LocalDateTime.of(2023, 12, 1, 10, 0),
                "ICN", "NRT",
                Duration.ofHours(2), "Korean Air");

        // 탑승자 정보 생성
        List<PassengerDto> passengerDtoList = List.of(
                new PassengerDto("John", "Doe",
                        "1995-05-26",
                        "12A")
        );

        // BookingDetailsDto 반환
        BookingDetailsDto bookingDetailsDto = new BookingDetailsDto(
                "A1234", // 예약 번호
                bookingRequestInfo.bookingEmail(),
                flightDto,
                passengerDtoList
        );

        return ResponseEntity.ok(bookingDetailsDto);
    }

    @GetMapping
    public ResponseEntity<BookingDetailsDto> getBookingDetails(
            @RequestParam("referenceCode") String referenceCode,
            @RequestParam("bookingEmail") String bookingEmail) {


        FlightDto flightDto = new FlightDto(123L,
                "FL123", BigDecimal.valueOf(500),
                LocalDateTime.of(2023, 12, 1, 8, 0),
                LocalDateTime.of(2023, 12, 1, 10, 0),
                "ICN", "NRT",
                Duration.ofHours(2), "Korean Air");

        List<PassengerDto> passengerDtoList = List.of(
                new PassengerDto(
                        "John", "Doe",
                        "1995-05-26",
                        "12A")
        );

        BookingDetailsDto bookingDetails = new BookingDetailsDto(
                "B1234", // 예약 번호
                bookingEmail,
                flightDto,
                passengerDtoList
        );

        // bookingId가 일치하지 않을 경우 예외 처리
        if (!bookingDetails.referenceCode().equals(referenceCode)) {
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



}
