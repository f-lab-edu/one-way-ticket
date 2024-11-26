package org.onewayticket.service;

import lombok.RequiredArgsConstructor;
import org.onewayticket.domain.Booking;
import org.onewayticket.domain.Flight;
import org.onewayticket.dto.BookingDetailsDto;
import org.onewayticket.dto.BookingRequestDto;
import org.onewayticket.dto.FlightDto;
import org.onewayticket.dto.PassengerDto;
import org.onewayticket.repository.BookingRepository;
import org.onewayticket.repository.FlightRepository;
import org.onewayticket.repository.PassengerRepository;
import org.onewayticket.security.AuthService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final PassengerRepository passengerRepository;
    private final AuthService authService;

    public void createBooking(BookingRequestDto bookingRequestInfo, String flightId) {
        Booking booking = Booking.from(bookingRequestInfo, Long.valueOf(flightId));
        try {
            bookingRepository.save(booking);
        } catch (Exception e) {
            throw new RuntimeException("Booking 저장 중 예외가 발생했습니다: " + e.getMessage(), e);
        }

    }

    public boolean cancelBooking(String id, String token) {
        Booking booking = bookingRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new IllegalArgumentException("해당 예약 정보가 없습니다."));
        String expectedToken = authService.generateToken(booking.getReferenceCode(), booking.getBookingEmail());
        if (expectedToken.equals(token)) {
            bookingRepository.delete(booking);
            return true;
        } else {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

    }

    public BookingDetailsDto getBookingDetails(String referenceCode) {
        // Booking 조회
        Booking booking = bookingRepository.findByReferenceCode(referenceCode)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        /**
         * Flight 조회 및 DTO 변환
         * mapping 관계 고민
         */
        Flight flight = flightRepository.findById(booking.getFlightId())
                .orElseThrow(() -> new RuntimeException("Flight not found"));

        FlightDto flightDto = new FlightDto(flight.getId(), flight.getFlightNumber(),
                flight.getAmount(), flight.getDepartureTime(), flight.getArrivalTime(),
                flight.getOrigin(), flight.getDestination(), Duration.ofMinutes(flight.getDurationInMinutes()), flight.getCarrier());


        // Passenger DTO 변환
        List<PassengerDto> passengerDtoList = booking.getPassengers()
                .stream()
                .map(passenger -> new PassengerDto(
                        passenger.getFirstName(),
                        passenger.getLastName(),
                        passenger.getDateOfBirth().toString(),
                        passenger.getPassportNumber(),
                        passenger.getGender(),
                        passenger.getSeatNumber()
                ))
                .toList();

        // BookingDetailsDto 반환
        return new BookingDetailsDto(
                booking.getId(),
                booking.getReferenceCode(),
                booking.getBookingEmail(),
                flightDto,
                passengerDtoList
        );
    }
}
