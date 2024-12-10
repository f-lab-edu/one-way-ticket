package org.onewayticket.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onewayticket.domain.Booking;
import org.onewayticket.domain.BookingDetail;
import org.onewayticket.domain.Flight;
import org.onewayticket.domain.Passenger;
import org.onewayticket.enums.BookingStatus;
import org.onewayticket.repository.BookingRepository;
import org.onewayticket.security.JwtUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FlightService flightService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private BookingService bookingService;


    @Test
    @DisplayName("유효한 값을 넣었을 때 Booking 생성이 완료됩니다.")
    void Create_booking_successfully() {
        // given
        Booking booking = Booking.builder()
                .referenceCode("REF12345")
                .bookingEmail("test@example.com")
                .flightId(1L)
                .paymentKey("PAYMENT123")
                .passengers(List.of(Passenger.builder().firstName("SEONGHUN").lastName("KOO").build()))
                .status(BookingStatus.COMPLETED)
                .build();

        Mockito.when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        // when
        Booking result = bookingService.createBooking(
                "test@example.com",
                List.of(Passenger.builder().firstName("SEONGHUN").lastName("KOO").build()),
                "PAYMENT123",
                1L
        );

        // then
        assertNotNull(result);
        assertEquals("PAYMENT123", result.getPaymentKey());
        assertEquals("test@example.com", result.getBookingEmail());
    }

    @Test
    @DisplayName("Repository 단에서 Booking 저장 중 예외 발생시에는 DataIntegrityViolation 예외가 발생합니다.")
    void Create_booking_fails_due_to_database_error() {
        // given
        Mockito.when(bookingRepository.save(any(Booking.class)))
                .thenThrow(new DataAccessException("Database Error") {
                });

        // when & then
        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () ->
                bookingService.createBooking(
                        "test@example.com",
                        List.of(Passenger.builder().firstName("SEONGHUN").lastName("KOO").build()),
                        "PAYMENT123",
                        1L
                )
        );

        assertTrue(exception.getMessage().contains("Booking 저장 중 예외가 발생했습니다"));
    }

    @Test
    @DisplayName("토큰이 올바르다면 정상적으로 Booking을 취소할 수 있습니다.")
    void Cancel_booking_successfully() {
        // given
        String referenceCode = "REF12345";
        String bookingEmail = "test@example.com";
        Booking booking = Booking.builder()
                .referenceCode(referenceCode)
                .bookingEmail(bookingEmail)
                .build();
        String validToken = "2e27e2d2";

        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        // when
        bookingService.cancelBooking("1", validToken);

        // then
        Mockito.verify(bookingRepository, times(1)).save(booking);
    }


    @Test
    @DisplayName("유효하지 않은 토큰을 넣으면 토큰 유효 Exception이 반환됩니다.")
    void Cancel_booking_with_invalid_token() {
        // given
        Booking booking = Booking.builder()
                .referenceCode("REF12345")
                .bookingEmail("test@example.com")
                .build();

        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                bookingService.cancelBooking("1", "INVALID_TOKEN")
        );

        assertTrue(exception.getMessage().contains("유효하지 않은 토큰입니다."));
    }

    @Test
    @DisplayName("예약 ID가 존재하지 않으면 예약 정보 없음 예외가 발생합니다.")
    void Cancel_booking_with_nonexistent_id() {
        // given
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                bookingService.cancelBooking("1", "VALID_TOKEN")
        );

        assertTrue(exception.getMessage().contains("해당 예약 정보가 없습니다."));
    }

    @Test
    @DisplayName("존재하는 예약번호를 입력하면 Booking 세부 정보를 조회할 수 있습니다.")
    void Get_booking_details_successfully() {
        // given
        Booking booking = Booking.builder()
                .referenceCode("REF12345")
                .flightId(1L)
                .build();
        Flight flight = Flight.builder()
                .id(1L)
                .origin("ICN")
                .destination("JFK")
                .build();

        Mockito.when(bookingRepository.findByReferenceCode("REF12345")).thenReturn(Optional.of(booking));
        Mockito.when(flightService.getFlightDetails("1")).thenReturn(flight);

        // when
        BookingDetail result = bookingService.getBookingDetailsByReferenceCode("REF12345");

        // then
        assertNotNull(result);
        assertEquals("REF12345", result.getReferenceCode());
        assertEquals("ICN", result.getFlight().getOrigin());
        Mockito.verify(bookingRepository, times(1)).findByReferenceCode("REF12345");
        Mockito.verify(flightService, times(1)).getFlightDetails("1");
    }

    @Test
    @DisplayName("예약 코드가 DB에 존재하지 않는다면 예약 정보 없음 예외를 발생시킵니다.")
    void Get_booking_details_with_invalid_reference_code() {
        // given
        Mockito.when(bookingRepository.findByReferenceCode("INVALID_REF")).thenReturn(Optional.empty());

        // when & then
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                bookingService.getBookingDetailsByReferenceCode("INVALID_REF")
        );

        assertTrue(exception.getMessage().contains("예약 정보를 찾을 수 없습니다."));
    }
}
