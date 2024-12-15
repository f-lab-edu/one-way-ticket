package org.onewayticket.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.onewayticket.dto.MyPageDto;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MemberControllerIntegrationTest {

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
        jdbcTemplate.execute("TRUNCATE TABLE member");
        jdbcTemplate.execute("ALTER TABLE member AUTO_INCREMENT = 1");
    }

    private void insertTestData() {
        jdbcTemplate.execute("""
                    INSERT INTO member (id, username, password) VALUES
                    (1, 'test_user', 'password123');
                """);
    }

    @Test
    @DisplayName("사용자 마이페이지 정보 조회")
    void Get_my_page_info() {
        // Given
        String url = baseUrl + "/api/v1/member/info";
        HttpHeaders headers = new HttpHeaders();
        String token = "Bearer " + jwtUtil.generateToken("test_user", 24 * 60 * 60 * 1000);
        headers.set("Authorization", token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<MyPageDto> response = restTemplate.exchange(url, HttpMethod.GET, request, MyPageDto.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test_user", response.getBody().username());
    }
}
