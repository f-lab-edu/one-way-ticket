package org.onewayticket.saver;

import org.onewayticket.model.Flight;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;

public class DataSaver {

    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;

    public DataSaver(String dbUrl, String dbUser, String dbPassword) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    public void saveFlights(List<Flight> flights) {
        String sql = "INSERT INTO flight (flight_number, amount, departure_time, arrival_time, origin, destination, duration_in_minutes, carrier) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            System.out.println("connection = " + connection);
            for (Flight flight : flights) {
                statement.setString(1, flight.getFlightNumber());
                statement.setBigDecimal(2, flight.getAmount());
                statement.setTimestamp(3, Timestamp.valueOf(flight.getDepartureTime()));
                statement.setTimestamp(4, Timestamp.valueOf(flight.getArrivalTime()));
                statement.setString(5, flight.getOrigin());
                statement.setString(6, flight.getDestination());
                statement.setInt(7, flight.getDurationInMinutes());
                statement.setString(8, flight.getCarrier());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (Exception e) {
            throw new RuntimeException("Error", e);
        }
    }
}
