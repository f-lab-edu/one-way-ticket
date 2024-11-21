package org.onewayticket.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BookingDetailsDto(
        // 기본 예약 정보
        String bookingId,                       // 예약 ID
        String bookingName,                 // 예약자 이름
        String bookingEmail,                // 예약자 이메일
        String bookingPhoneNumber,          // 예약자 전화번호

        // 항공편 정보
        String flightId,                        // 항공편 ID
        String flightOrigin,                    // 출발 공항
        String flightDestination,               // 도착 공항
        LocalDate flightDepartureDate,          // 출발 날짜
        LocalDate flightArrivalDate,            // 도착 날짜

        // 탑승자 정보
        String passengerName,                   // 탑승자 이름
        LocalDate passengerBirthDate,           // 탑승자 생년월일
        int passengerAge,                       // 탑승자 나이
        String passengerGender,                 // 탑승자 성별
        String passengerPassportNumber,         // 여권 번호
        String passengerNationality,            // 국적
        String passengerSeatNumber,             // 좌석 번호
        String passengerSeatClass,              // 좌석 등급 (예: Economy, Business)

        // 결제 정보
        BigDecimal totalAmount,                 // 총 결제 금액
        String paymentStatus                    // 결제 상태
) {
}
