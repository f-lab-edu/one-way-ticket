package org.onewayticket.util;

import lombok.RequiredArgsConstructor;
import org.onewayticket.model.Flight;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FlightDataSaver {

    private final JdbcTemplate jdbcTemplate;

    public void saveFlights(List<Flight> flights) {
        String sql = "INSERT INTO flight (id, flight_number, amount, departure_time, arrival_time, origin, destination, duration_in_minutes, carrier) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, flights, 100, (ps, flight) -> {
            ps.setString(1, flight.getId());
            ps.setString(2, flight.getFlightNumber());
            ps.setBigDecimal(3, flight.getAmount());
            ps.setTimestamp(4, Timestamp.valueOf(flight.getDepartureTime()));
            ps.setTimestamp(5, Timestamp.valueOf(flight.getArrivalTime()));
            ps.setString(6, flight.getOrigin());
            ps.setString(7, flight.getDestination());
            ps.setInt(8, flight.getDurationInMinutes());
            ps.setString(9, flight.getCarrier());
        });
    }
}
