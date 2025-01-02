import org.junit.jupiter.api.Test;
import org.onewayticket.generator.FlightDataGenerator;
import org.onewayticket.model.Flight;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FlightDataGeneratorTest {

    @Test
    public void testGenerateRandomFlights_GivenValidCount_WhenCalled_ThenReturnListOfFlights() {
        // Given
        FlightDataGenerator generator = new FlightDataGenerator();
        int flightCount = 5;

        // When
        List<Flight> flights = generator.generateRandomFlights(flightCount);

        // Then
        assertNotNull(flights, "Flight list should not be null");
        assertEquals(flightCount, flights.size(), "Flight list size should match requested count");

        for (Flight flight : flights) {
            assertNotNull(flight.getFlightNumber(), "Flight number should not be null");
            assertNotNull(flight.getAmount(), "Flight amount should not be null");
            assertNotNull(flight.getDepartureTime(), "Departure time should not be null");
            assertNotNull(flight.getArrivalTime(), "Arrival time should not be null");
            assertNotNull(flight.getOrigin(), "Origin should not be null");
            assertNotNull(flight.getDestination(), "Destination should not be null");
            assertTrue(flight.getDurationInMinutes() > 0, "Flight duration should be positive");
            assertNotNull(flight.getCarrier(), "Carrier should not be null");
        }
    }
}
