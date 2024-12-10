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

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    @DisplayName("회원가입 성공")
    void Register_Success() {
        // Given
        String url = baseUrl + "/api/v1/auth/register";
        UserInfoDto requestDto = new UserInfoDto("testuser", "password123");

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestDto, String.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("회원가입이 완료되었습니다.", response.getBody());
    }

    @Test
    @DisplayName("로그인 성공")
    void Login_Success() {
        // Given
        String registerUrl = baseUrl + "/api/v1/auth/register";
        String loginUrl = baseUrl + "/api/v1/auth/login";

        UserInfoDto registerDto = new UserInfoDto("testuser", "password123");
        restTemplate.postForEntity(registerUrl, registerDto, String.class);
        UserInfoDto loginDto = new UserInfoDto("testuser", "password123");

        // When
        ResponseEntity<Map> response = restTemplate.postForEntity(loginUrl, loginDto, Map.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().get("token"));
    }

    @Test
    @DisplayName("로그인 실패 - 회원 없음")
    void Login_Fail_UserNotFound() {
        // Given
        String loginUrl = baseUrl + "/api/v1/auth/login";
        UserInfoDto loginDto = new UserInfoDto("nonexistentuser", "password123");

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, loginDto, String.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void Login_Fail_InvalidPassword() {
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
