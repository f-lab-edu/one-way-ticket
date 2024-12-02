package org.onewayticket.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Getter
public class BookingResponse {
    private BookingDetail bookingDetail;
    private String token;

}
