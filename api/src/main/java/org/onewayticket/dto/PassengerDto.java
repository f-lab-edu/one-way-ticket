package org.onewayticket.dto;

import org.onewayticket.domain.Passenger;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public record PassengerDto(
        String firstName,  // 탑승자 성
        String lastName,   // 탑승자 이름
        String birthDate,  // 탑승자 생년월일
        String passportNumber,
        String gender,
        String seatNumber  // 좌석 번호
) {
    public static PassengerDto from(Passenger passenger) {
        return new PassengerDto(
                passenger.getFirstName(),
                passenger.getLastName(),
                passenger.getDateOfBirth().toString(), // LocalDate -> String 변환
                passenger.getPassportNumber(),
                passenger.getGender(),
                passenger.getSeatNumber()
        );
    }

    public static Passenger from(PassengerDto dto) {
        return Passenger.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .passportNumber(dto.passportNumber())
                .dateOfBirth(LocalDate.parse(dto.birthDate()))
                .build();
    }

    public static List<Passenger> from(List<PassengerDto> dtoList) {
        return dtoList.stream()
                .map(PassengerDto::from)
                .collect(Collectors.toList());
    }
}