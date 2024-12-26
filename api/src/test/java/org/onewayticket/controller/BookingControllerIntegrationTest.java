package org.onewayticket.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.onewayticket.dto.BookingDetailsDto;
import org.onewayticket.dto.BookingRequestDto;
import org.onewayticket.dto.BookingResponseDto;
import org.onewayticket.dto.PassengerDto;
import org.onewayticket.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookingControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    private String baseUrl;

    @BeforeEach
    void setup() {
        baseUrl = "http://localhost:" + port;
        resetDatabase();
        insertTestData();
    }

    private void resetDatabase() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.execute("TRUNCATE TABLE passenger");
        jdbcTemplate.execute("TRUNCATE TABLE booking");
        jdbcTemplate.execute("TRUNCATE TABLE flight");
        jdbcTemplate.execute("TRUNCATE TABLE member");
        jdbcTemplate.execute("ALTER TABLE passenger AUTO_INCREMENT = 1");
        jdbcTemplate.execute("ALTER TABLE booking AUTO_INCREMENT = 1");
        jdbcTemplate.execute("ALTER TABLE flight AUTO_INCREMENT = 1");
        jdbcTemplate.execute("ALTER TABLE member AUTO_INCREMENT = 1");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    private void insertTestData() {
        jdbcTemplate.execute("""
                    INSERT INTO flight (flight_number, amount, departure_time, arrival_time, origin, destination, duration_in_minutes, carrier) VALUES
                    ('AA101', 150.00, '2024-12-01 08:00:00', '2024-12-01 11:00:00', 'ICN', 'LAX', 180, 'American Airlines'),
                    ('UA202', 200.00, '2024-12-01 09:00:00', '2024-12-01 13:00:00', 'ICN', 'ORD', 240, 'United Airlines'),
                    ('DL303', 175.50, '2024-12-02 14:00:00', '2024-12-02 18:00:00', 'ICN', 'SEA', 240, 'Delta Airlines');
                """);

        jdbcTemplate.execute("""
                    INSERT INTO booking (member_id, reference_code, booking_email, flight_id, payment_key, status, created_at) VALUES
                    (1, 'B1234', 'johndoe@example.com', 1, '456', 'CONFIRMED', '2023-11-25 14:30:00'),
                    (2, 'B1235', 'alice@example.com', 2, '457', 'CONFIRMED', '2023-11-26 09:00:00'),
                    (3, 'B1236', 'bob@example.com', 3, '458', 'PENDING', '2023-11-27 13:45:00');
                """);

        jdbcTemplate.execute("""
                    INSERT INTO passenger (first_name, last_name, passport_number, gender, seat_number, date_of_birth, booking_id) VALUES
                    ('John', 'Doe', 'A12345678', 'Male', '12A', '1995-05-26', 1),
                    ('Jane', 'Doe', 'B98765432', 'Female', '12B', '1998-03-14', 1),
                    ('Alice', 'Smith', 'C87654321', 'Female', '14A', '1992-08-15', 2);
                """);

        jdbcTemplate.execute("""
                    INSERT INTO member (username, password) VALUES
                    ('johndoe@example.com', '1234')
                """);
    }

    @Test
    @DisplayName("유효한 정보 입력시 예약이 완료됩니다.")
    void createBookingWithValidInfo() {
        // Given
        String url = baseUrl + "/api/v1/bookings?flightId=1";
        BookingRequestDto request = new BookingRequestDto(
                "johndoe@example.com",
                List.of(new PassengerDto("John", "Doe", "1995-05-01", "M34993022", "male", "12A")),
                "paymentKey"
        );

        // When
        ResponseEntity<BookingDetailsDto> response = restTemplate.postForEntity(url, request, BookingDetailsDto.class);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("johndoe@example.com", response.getBody().bookingEmail());
    }

    @Test
    @DisplayName("유효하지 않은 Payment ID 입력시 예약이 되지 않습니다.")
    void createBookingWithInvalidPaymentId() {
        // Given
        String url = baseUrl + "/api/v1/bookings";
        BookingRequestDto request = new BookingRequestDto(
                "john.doe@example.com",
                List.of(new PassengerDto("John", "Doe", "1995-05-01", "M34993022", "male", "12A")),
                "" // payment ID 누락
        );

        // When
        ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("회원은 현재 예약 목록을 조회할 수 있습니다.")
    void getBookingDetailsList() {
        // Given
        String username = "johndoe@example.com";
        String url = baseUrl + "/api/v1/bookings";
        String token = jwtUtil.generateToken(username, 60 * 10 * 1000);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // When
        ResponseEntity<BookingDetailsDto[]> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, BookingDetailsDto[].class);
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().length);
    }

    @Test
    @DisplayName("회원은 자신이 예약한 특정 예약의 상세정보를 확인할 수 있습니다.")
    void getBookingDetailsForMember() {
        // Given
        Long bookingId = 1L;
        String username = "johndoe@example.com";
        String url = baseUrl + "/api/v1/bookings/" + bookingId;
        String token = jwtUtil.generateToken(username, 60 * 10 * 1000);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // When
        ResponseEntity<BookingDetailsDto> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, BookingDetailsDto.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("기존 예약 고객은 예약정보를 조회할 수 있습니다.")
    void getBookingDetailsWithValidInfoSync() {
        // Given
        String url = baseUrl + "/api/v1/bookings/guest?referenceCode=B1234&bookingEmail=johndoe@example.com";

        // When
        ResponseEntity<BookingDetailsDto> response = restTemplate.getForEntity(url, BookingDetailsDto.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getHeaders().getFirst("Authorization"));
        assertEquals("B1234", response.getBody().referenceCode());
    }

    @Test
    @DisplayName("존재하지 않는 예약 정보로 조회할 때는 404를 반환합니다.")
    void Get_bookingDetails_with_invalid_info_not_found() {
        // Given
        String url = baseUrl + "/api/v1/bookings/guest?referenceCode=B12345&bookingEmail=\"johndoe@example.com\"";

        // When
        ResponseEntity<?> response = restTemplate.getForEntity(url, BookingResponseDto.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("예약 취소 API 요청이 성공적으로 처리됩니다.")
    void cancelBookingWithValidData() {
        // Given
        String bookingId = "1";
        String username = "johndoe@example.com";
        String url = baseUrl + "/api/v1/bookings/" + bookingId;
        String token = jwtUtil.generateToken(username, 60 * 10 * 1000);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("해당 예약이 취소되었습니다.", response.getBody());
    }

}
