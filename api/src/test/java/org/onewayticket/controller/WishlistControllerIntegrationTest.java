package org.onewayticket.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.onewayticket.domain.Wishlist;
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

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WishlistControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtUtil jwtUtil;

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
        jdbcTemplate.execute("TRUNCATE TABLE wishlist");
        jdbcTemplate.execute("ALTER TABLE wishlist AUTO_INCREMENT = 1");
    }

    private void insertTestData() {
        jdbcTemplate.execute("""
                    INSERT INTO wishlist (member_id, flight_id, created_at) VALUES
                    (1, 1001, NOW()),
                    (1, 1002, NOW());
                """);
    }

    @Test
    @DisplayName("찜 목록 조회")
    void Get_my_wishlist() {
        // Given
        String url = baseUrl + "/api/v1/wishlist";
        HttpHeaders headers = new HttpHeaders();
        String token = "Bearer " + jwtUtil.generateToken("test_user", 24 * 60 * 60 * 1000);
        headers.set("Authorization", token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<Wishlist[]> response = restTemplate.exchange(url, HttpMethod.GET, request, Wishlist[].class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0, "찜 목록이 비어 있습니다.");
    }

    @Test
    @DisplayName("찜 목록에 항공편 추가")
    void Add_to_wishlist() {
        // Given
        String url = baseUrl + "/api/v1/wishlist/1003";
        HttpHeaders headers = new HttpHeaders();
        String token = "Bearer " + jwtUtil.generateToken("test_user", 24 * 60 * 60 * 1000);
        headers.set("Authorization", token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("찜 목록에 추가되었습니다.", response.getBody());
    }

    @Test
    @DisplayName("찜 목록에서 항공편 제거")
    void Remove_from_wishlist() {
        // Given
        String url = baseUrl + "/api/v1/wishlist/1";
        HttpHeaders headers = new HttpHeaders();
        String token = "Bearer " + jwtUtil.generateToken("test_user", 24 * 60 * 60 * 1000);
        headers.set("Authorization", token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("찜 목록에서 제거되었습니다.", response.getBody());
    }
}
