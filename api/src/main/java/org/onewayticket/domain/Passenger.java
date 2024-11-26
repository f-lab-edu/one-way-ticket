package org.onewayticket.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.onewayticket.dto.PassengerDto;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String referenceCode;
    private String firstName;
    private String lastName;
    private String passportNumber;
    private String gender;
    private String seatNumber;
    private LocalDate dateOfBirth;

    public static Passenger from(PassengerDto dto) {
        return Passenger.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .passportNumber(dto.passportNumber())
                .dateOfBirth(LocalDate.parse(dto.birthDate()))
                .build();
    }

}
