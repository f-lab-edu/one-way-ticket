package org.onewayticket.repository;

import org.onewayticket.domain.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    Optional<List<Flight>> findByOriginOrderByAmountAsc(String origin);

    Optional<List<Flight>> findByOriginAndDestinationAndDepartureTime(
            String origin,
            String destination,
            LocalDate departureDate
    );



}
