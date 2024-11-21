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

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FlightControllerIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setup() {
        baseUrl = "http://localhost:" + port;
    }

    @Test
    @DisplayName("저렴한 순으로 상위 9개 목적지에 대한 항공권 정보를 반환합니다.")
    void Get_cheapest_flights() {
        // Given
        String url = baseUrl + "/api/v1/flights/cheapest";

        // When
        ResponseEntity<FlightDto[]> response = restTemplate.getForEntity(url, FlightDto[].class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
        assertTrue(response.getBody()[0].price().compareTo(response.getBody()[1].price()) <= 0);
    }


    @Test
    @DisplayName("입력한 출발지와 목적지에 해당하는 항공편이 리턴됩니다.")
    void Search_flights_by_valid_info() {
        // Given
        String todayDate = LocalDate.now().toString(); // 현재 날짜를 "yyyy-MM-dd" 형식으로 가져옴
        String url = baseUrl + "/api/v1/flights/search?departure=ICN&destination=NRT&departureDate=" + todayDate + "&numberOfPassengers=1&sort=price";

        // When
        ResponseEntity<FlightDto[]> response = restTemplate.getForEntity(url, FlightDto[].class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ICN", response.getBody()[0].origin());
        assertEquals("NRT", response.getBody()[0].destination());
        assertEquals(todayDate, response.getBody()[0].departureTime().toLocalDate().toString());
        assertTrue(response.getBody()[0].price().compareTo(response.getBody()[1].price()) <= 0);
    }

    @Test
    @DisplayName("가격 정렬 필터를 넣으면 최저가 순으로 정렬되어 반환됩니다.")
    void Search_flights_sorted_by_price() {
        // Given
        String url = baseUrl + "/api/v1/flights/search?departure=ICN&destination=NRT&departureDate=2023-12-01&numberOfPassengers=1&sort=price";

        // When
        ResponseEntity<FlightDto[]> response = restTemplate.getForEntity(url, FlightDto[].class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody()[0].price().compareTo(response.getBody()[1].price()) <= 0);
    }

    @Test
    @DisplayName("도착시간 필터를 넣으면 가장 빠른 도착시간 순으로 정렬되어 반환됩니다.")
    void Search_flights_sorted_By_arrivaltime() {
        // Given
        String url = baseUrl + "/api/v1/flights/search?departure=ICN&destination=NRT&departureDate=2023-12-01&numberOfPassengers=1&sort=arrivalTime";

        // When
        ResponseEntity<FlightDto[]> response = restTemplate.getForEntity(url, FlightDto[].class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody()[0].arrivalTime().compareTo(response.getBody()[1].arrivalTime()) <= 0);
    }

    @Test
    @DisplayName("최소 비행시간 필터를 넣으면 가장 짧은 비행시간 순으로 정렬되어 반환됩니다.")
    void search_flights_sorted_by_flight_duration() {
        // Given
        String url = baseUrl + "/api/v1/flights/search?departure=ICN&destination=NRT&departureDate=2023-12-01&numberOfPassengers=1&sort=flightDuration";

        // When
        ResponseEntity<FlightDto[]> response = restTemplate.getForEntity(url, FlightDto[].class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody()[0].flightDuration().compareTo(response.getBody()[1].flightDuration()) <= 0);
    }


    @Test
    @DisplayName("유효한 flightId로 항공권 조회가 가능합니다.")
    void Get_flightDetails_with_valid_flightId() {
        // Given
        String url = baseUrl + "/api/v1/flights/FL001";

        // When
        ResponseEntity<FlightDto> response = restTemplate.getForEntity(url, FlightDto.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("FL001", response.getBody().flightId());
    }

    @Test
    @DisplayName("유효하지 않은 flightId는 400을 반환합니다.")
    void testGetFlightDetailsBadRequest() {
        // Given
        String url = baseUrl + "/api/v1/flights/" + "A".repeat(51);

        // When
        ResponseEntity<Void> response = restTemplate.getForEntity(url, Void.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


}
