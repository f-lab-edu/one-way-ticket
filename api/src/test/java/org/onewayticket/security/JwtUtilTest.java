package org.onewayticket.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(new ObjectMapper());
    }

    @Test
    @DisplayName("토큰 생성 테스트")
    void testGenerateToken() {
        // Given
        String username = "testuser";
        long expirationMillis = 60000; // 1분

        // When
        String token = jwtUtil.generateToken(username, expirationMillis);

        // Then
        assertNotNull(token, "토큰이 생성되지 않았습니다.");
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT 구조가 올바르지 않습니다.");

        // Header 검증
        String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
        assertTrue(headerJson.contains("\"alg\":\"HS256\""), "Header에 알고리즘 정보가 없습니다.");
        assertTrue(headerJson.contains("\"typ\":\"JWT\""), "Header에 토큰 타입 정보가 없습니다.");

        // Payload 검증
        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
        assertTrue(payloadJson.contains("\"sub\":\"" + username + "\""), "Payload에 사용자 정보가 없습니다.");
    }

    @Test
    @DisplayName("유효한 토큰 검증 테스트")
    void testValidateToken() {
        // Given
        String username = "testuser";
        long expirationMillis = 60000; // 1분
        String token = jwtUtil.generateToken(username, expirationMillis);

        // When
        boolean isValid = jwtUtil.validateToken(token);

        // Then
        assertTrue(isValid, "유효한 토큰이 검증되지 않았습니다.");
    }

    @Test
    @DisplayName("만료된 토큰 검증 테스트")
    void testValidateExpiredToken() throws InterruptedException {
        // Given
        String username = "testuser";
        long expirationMillis = 1000; // 1초
        String token = jwtUtil.generateToken(username, expirationMillis);

        // 토큰 만료를 기다림
        Thread.sleep(2000);

        // When
        boolean isValid = jwtUtil.validateToken(token);

        // Then
        assertFalse(isValid, "만료된 토큰이 유효하다고 검증되었습니다.");
    }

    @Test
    @DisplayName("위조된 토큰 검증 테스트")
    void testValidateTamperedToken() {
        // Given
        String username = "testuser";
        long expirationMillis = 60000;
        String token = jwtUtil.generateToken(username, expirationMillis);

        // 위조된 서명을 추가
        String tamperedToken = token.substring(0, token.lastIndexOf('.') + 1) + "tampered-signature";

        // When
        boolean isValid = jwtUtil.validateToken(tamperedToken);

        // Then
        assertFalse(isValid, "위조된 토큰이 유효하다고 검증되었습니다.");
    }

    @Test
    @DisplayName("토큰에서 사용자 이름 추출 테스트")
    void testGetUsernameFromToken() {
        // Given
        String username = "testuser";
        long expirationMillis = 60000;
        String token = jwtUtil.generateToken(username, expirationMillis);

        // When
        String extractedUsername = jwtUtil.getUsername(token);

        // Then
        assertEquals(username, extractedUsername, "추출된 사용자 이름이 일치하지 않습니다.");
    }

    @Test
    @DisplayName("유효하지 않은 토큰에서 사용자 이름 추출 시 예외 발생 테스트")
    void testGetUsernameFromInvalidToken() {
        // Given
        String invalidToken = "invalid.token.structure";

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> jwtUtil.getUsername(invalidToken));
        assertTrue(exception.getMessage().contains("유효하지 않은 토큰입니다."), "예외 메시지가 올바르지 않습니다.");
    }
}