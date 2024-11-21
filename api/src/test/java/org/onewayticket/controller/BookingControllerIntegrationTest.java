package org.onewayticket.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.onewayticket.dto.BookingDetailsDto;
import org.onewayticket.dto.BookingRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookingControllerIntegrationTest {

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
    @DisplayName("유효한 정보 입력시 예약이 완료됩니다.")
    void Create_booking_with_validate_info() {
        // Given
        String url = baseUrl + "/api/v1/bookings";
        BookingRequestDto request = new BookingRequestDto(
                "John Doe", "john.doe@example.com", "123456789", "FL123", "1995-05-01"
                , "Confirmed"
        );

        // When
        ResponseEntity<BookingDetailsDto> response = restTemplate.postForEntity(url, request, BookingDetailsDto.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John Doe", response.getBody().bookingName());
    }

    @Test
    @DisplayName("유효하지 않은 Payment ID 입력시 예약이 되지 않습니다.")
    void Create_Booking_With_Invalid_PaymentId() {
        // Given
        String url = baseUrl + "/api/v1/bookings";
        BookingRequestDto request = new BookingRequestDto(
                "John Doe", "john.doe@example.com", "123456789", "FL123",
                "1995-05-01", "INVALID*ID"  // 유효하지 않은 결제 ID
        );

        // When
        ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("기존 예약 고객은 예약번호, 이름, 생년월일을 통해 예약정보를 조회할 수 있습니다.")
    void Get_bookingDetails_with_valid_info() {
        // Given
        String url = baseUrl + "/api/v1/bookings?bookingId=B1234&name=John Doe&birthDate=1995-05-01";

        // When
        ResponseEntity<BookingDetailsDto> response = restTemplate.getForEntity(url, BookingDetailsDto.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("B1234", response.getBody().bookingId());
        assertEquals("John Doe", response.getBody().passengerName());
    }

    @Test
    @DisplayName("존재하지 않는 예약 정보로 조회할 때는 404를 반환합니다.")
    void Get_bookingDetails_with_invalid_info_not_found() {
        // Given
        String url = baseUrl + "/api/v1/bookings?bookingId=B9999&name=John Smith&birthDate=1980-01-01";

        // When
        ResponseEntity<Void> response = restTemplate.getForEntity(url, Void.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("잘못된 입력값으로 예약 조회할 때는 400을 반환합니다.")
    void Get_bookingDetails_with_invalid_input_bad_request() {
        // Given
        String url = baseUrl + "/api/v1/bookings?bookingId=&name=&birthDate=9999-01-01";

        // When
        ResponseEntity<Void> response = restTemplate.getForEntity(url, Void.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("사용자의 정보로 생성된 유효한 토큰으로 예약을 취소할 수 있습니다.")
    void Delete_booking_with_valid_token() {
        // Given
        String url = baseUrl + "/api/v1/bookings/123";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer VALID_TOKEN");

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("has been canceled successfully"));
    }


}
