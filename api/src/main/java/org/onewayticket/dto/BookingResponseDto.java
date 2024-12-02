package org.onewayticket.dto;

import org.onewayticket.domain.BookingResponse;

public record BookingResponseDto(
        String token,
        BookingDetailsDto bookingDetails)
{
    public static BookingResponseDto from(BookingResponse bookingResponse){
        return new BookingResponseDto(bookingResponse.getToken(), BookingDetailsDto.from(bookingResponse.getBookingDetail()));

}
}
