package org.onewayticket.dto;

public record BookingResponseDto(
        String token,
        BookingDetailsDto bookingDetails)
{
}
