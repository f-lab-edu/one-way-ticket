package org.onewayticket.dto;

import org.onewayticket.domain.BookingDetail;

import java.util.List;

public record BookingDetailsDto(
        Long id,
        String referenceCode,               // 예약 번호(사용자 조회용)
        String bookingEmail,                // 예약자 이메일
        FlightDto flightDto,                // 항공편 정보
        List<PassengerDto> passengerDtoList// 탑승자 정보
) {
    public static BookingDetailsDto from(BookingDetail bookingDetail) {
        return new BookingDetailsDto(
                bookingDetail.getBookingId(),
                bookingDetail.getReferenceCode(),
                bookingDetail.getBookingEmail(),
                FlightDto.from(bookingDetail.getFlight()),
                bookingDetail.getPassengers().stream()
                        .map(PassengerDto::from)
                        .toList()
        );
    }

    public static List<BookingDetailsDto> fromList(List<BookingDetail> bookingDetails) {
        return bookingDetails.stream()
                .map(BookingDetailsDto::from)
                .toList();
    }
}
