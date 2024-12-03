package org.onewayticket.repository;

import org.onewayticket.domain.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    Optional<List<Flight>> findByOriginOrderByAmountAsc(String origin);

    @Query("""
    SELECT f FROM Flight f
    WHERE (:origin IS NULL OR f.origin = :origin)
      AND (:destination IS NULL OR f.destination = :destination)
      AND (:departureDate IS NULL OR CAST(f.departureTime AS date) = :departureDate)
    """)
    Optional<List<Flight>> searchFlights(
            @Param("origin") String origin,
            @Param("destination") String destination,
            @Param("departureDate") LocalDate departureDate
    );




}
