package org.onewayticket.dto;

public record PassengerDto(
        String firstName,  // 탑승자 성
        String lastName,   // 탑승자 이름
        String birthDate,  // 탑승자 생년월일
        String seatNumber  // 좌석 번호
) {
}