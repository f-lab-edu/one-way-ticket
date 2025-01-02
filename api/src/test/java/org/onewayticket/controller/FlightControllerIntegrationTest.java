package org.onewayticket.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.onewayticket.dto.FlightDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FlightControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String baseUrl;

    @BeforeEach
    void setup() {
        baseUrl = "http://localhost:" + port;
        resetDatabase();
        insertTestData();
    }

    private void resetDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE flight");
    }

    private void insertTestData() {
        jdbcTemplate.execute("""
                INSERT INTO flight (id, flight_number, amount, departure_time, arrival_time, origin, destination, duration_in_minutes, carrier) VALUES
                ('FLIGHT001', 'AA101', 150.00, '2024-12-01 08:00:00', '2024-12-01 11:00:00', 'ICN', 'LAX', 180, 'American Airlines'),
                ('FLIGHT002', 'UA202', 200.00, '2024-12-01 09:00:00', '2024-12-01 13:00:00', 'ICN', 'ORD', 240, 'United Airlines'),
                ('FLIGHT003', 'DL303', 175.50, '2024-12-02 14:00:00', '2024-12-02 18:00:00', 'ICN', 'SEA', 240, 'Delta Airlines');
            """);
    }


    @Test
    @DisplayName("저렴한 순으로 상위 항공권 정보를 반환합니다.")
    void Get_cheapest_flights() {
        // Given
        String url = baseUrl + "/api/v1/flights/cheapest";

        // When
        ResponseEntity<FlightDto[]> response = restTemplate.getForEntity(url, FlightDto[].class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0, "항공권 정보가 반환되지 않았습니다.");
        assertTrue(response.getBody()[0].amount().compareTo(response.getBody()[1].amount()) <= 0, "가격이 오름차순으로 정렬되지 않았습니다.");
    }

    @Test
    @DisplayName("유효한 출발지와 목적지로 항공편 검색")
    void Search_flights_by_valid_info() {
        // Given
        String url = baseUrl + "/api/v1/flights/search?origin=ICN&destination=LAX&departureDate=2024-12-01&sort=price";

        String jsonResponse = restTemplate.getForObject(url, String.class);
        // When
        ResponseEntity<FlightDto[]> response = restTemplate.getForEntity(url, FlightDto[].class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ICN", response.getBody()[0].origin());
        assertEquals("LAX", response.getBody()[0].destination());
    }

    @Test
    @DisplayName("유효한 flightId로 항공권 상세 조회")
    void Get_flightDetails_with_valid_flightId() {
        // Given
        Long flightId = 1L;
        String url = baseUrl + "/api/v1/flights/" + flightId;

        // When
        ResponseEntity<FlightDto> response = restTemplate.getForEntity(url, FlightDto.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("AA101", response.getBody().flightNumber());
    }

    @Test
    @DisplayName("유효하지 않은 flightId로 항공권 조회")
    void Get_flightDetails_with_invalid_flightId() {
        // Given
        String url = baseUrl + "/api/v1/flights/9999";

        // When
        ResponseEntity<Void> response = restTemplate.getForEntity(url, Void.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
