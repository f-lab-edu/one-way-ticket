import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onewayticket.model.Flight;
import org.onewayticket.saver.DataSaver;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataSaverTest {

    private static final String DB_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    private DataSaver dataSaver;

    @BeforeEach
    public void setupDatabase() throws Exception {

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement()) {
            String createTableSql = """
                        CREATE TABLE IF NOT EXISTS flight (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            flight_number VARCHAR(255),
                            amount DECIMAL(19, 2),
                            departure_time TIMESTAMP,
                            arrival_time TIMESTAMP,
                            origin VARCHAR(255),
                            destination VARCHAR(255),
                            duration_in_minutes INT,
                            carrier VARCHAR(255)
                        );
                    """;
            statement.execute(createTableSql);
        }

        dataSaver = new DataSaver(DB_URL, DB_USER, DB_PASSWORD);
    }

    @Test
    public void testSaveFlights() throws Exception {
        // Given
        List<Flight> flights = List.of(
                Flight.builder()
                        .flightNumber("ABC123")
                        .amount(BigDecimal.valueOf(100.00))
                        .departureTime(LocalDateTime.now())
                        .arrivalTime(LocalDateTime.now().plusHours(1))
                        .origin("ICN")
                        .destination("JFK")
                        .durationInMinutes(120)
                        .carrier("Korean Air")
                        .build(),
                Flight.builder()
                        .flightNumber("DEF456")
                        .amount(BigDecimal.valueOf(200.00))
                        .departureTime(LocalDateTime.now())
                        .arrivalTime(LocalDateTime.now().plusHours(2))
                        .origin("LAX")
                        .destination("NRT")
                        .durationInMinutes(180)
                        .carrier("Delta")
                        .build()
        );

        // When
        dataSaver.saveFlights(flights);

        // Then
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement();
             var resultSet = statement.executeQuery("SELECT COUNT(*) AS count FROM flight")) {

            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                assertEquals(2, count);
            }
        }
    }
}