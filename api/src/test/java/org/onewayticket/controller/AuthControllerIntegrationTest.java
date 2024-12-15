package org.onewayticket.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.onewayticket.dto.UserInfoDto;
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
public class AuthControllerIntegrationTest {

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
    }

    private void resetDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE member");
        jdbcTemplate.execute("ALTER TABLE member AUTO_INCREMENT = 1");
    }

    @Test
    @DisplayName("회원가입 성공 시 로그인도 동시에 이루어집니다.")
    void registerWithValidInfo() {
        // Given
        String url = baseUrl + "/api/v1/auth/register";
        UserInfoDto requestDto = new UserInfoDto("testuser", "password123");

        // When
        ResponseEntity<Void> response = restTemplate.postForEntity(url, requestDto, Void.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getHeaders().getFirst("Authorization"));
        assertTrue(response.getHeaders().getFirst("Authorization").startsWith("Bearer "));
    }

    @Test
    @DisplayName("로그인 성공 시에는 클라이언트에게 jwt을 반환합니다.")
    void loginWithValidInfo() {
        // Given
        String registerUrl = baseUrl + "/api/v1/auth/register";
        String loginUrl = baseUrl + "/api/v1/auth/login";

        UserInfoDto registerDto = new UserInfoDto("testuser", "password123");
        restTemplate.postForEntity(registerUrl, registerDto, Void.class);

        UserInfoDto loginDto = new UserInfoDto("testuser", "password123");

        // When
        ResponseEntity<Void> response = restTemplate.postForEntity(loginUrl, loginDto, Void.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getHeaders().getFirst("Authorization"));
        assertTrue(response.getHeaders().getFirst("Authorization").startsWith("Bearer "));
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 로그인 시 로그인 실패합니다.")
    void loginFailUserNotFound() {
        // Given
        String loginUrl = baseUrl + "/api/v1/auth/login";
        UserInfoDto loginDto = new UserInfoDto("nonexistentuser", "password123");

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, loginDto, String.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("비밀번호 불일치하다면 로그인이 실패합니다.")
    void loginFailWhenPasswordIsWrong() {
        // Given
        String registerUrl = baseUrl + "/api/v1/auth/register";
        String loginUrl = baseUrl + "/api/v1/auth/login";

        UserInfoDto registerDto = new UserInfoDto("testuser", "password123");
        restTemplate.postForEntity(registerUrl, registerDto, String.class);

        UserInfoDto loginDto = new UserInfoDto("testuser", "wrongpassword");

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, loginDto, String.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
