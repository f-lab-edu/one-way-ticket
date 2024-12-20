package org.onewayticket.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onewayticket.domain.Member;
import org.onewayticket.repository.MemberRepository;
import org.onewayticket.security.JwtUtil;
import org.onewayticket.security.PasswordUtil;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("회원가입 성공")
    void Register_Success() {
        // Given
        String username = "testuser";
        String password = "password123";
        Member mockMember = Member.builder()
                .username(username)
                .password(PasswordUtil.hashPassword(password))
                .build();

        Mockito.when(memberRepository.save(Mockito.any(Member.class))).thenReturn(mockMember);

        // When
        Member result = authService.register(username, password);

        // Then
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        Mockito.verify(memberRepository, times(1)).save(Mockito.any(Member.class));
    }

    @Test
    @DisplayName("로그인 성공")
    void Authenticate_Success() {
        // Given
        String username = "testuser";
        String password = "password123";
        Member mockMember = Member.builder()
                .username(username)
                .password(PasswordUtil.hashPassword(password))
                .build();

        Mockito.when(memberRepository.findByUsername(username)).thenReturn(Optional.of(mockMember));
        Mockito.when(jwtUtil.generateToken(username, 24 * 60 * 60 * 1000)).thenReturn("mocked-token"); // Mock 설정

        // When
        String result = authService.authenticate(username, password);

        // Then
        assertNotNull(result);
        Mockito.verify(memberRepository, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("로그인 실패 - 회원 없음")
    void Authenticate_Fail_UserNotFound() {
        // Given
        String username = "nonexistentuser";
        String password = "password123";

        Mockito.when(memberRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> authService.authenticate(username, password));
        assertEquals("회원을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void Authenticate_Fail_InvalidPassword() {
        // Given
        String username = "testuser";
        String password = "password123";
        String wrongPassword = "wrongpassword";

        Member mockMember = Member.builder()
                .username(username)
                .password(PasswordUtil.hashPassword(password))
                .build();

        Mockito.when(memberRepository.findByUsername(username)).thenReturn(Optional.of(mockMember));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.authenticate(username, wrongPassword));
        assertEquals("아이디 비밀번호가 올바르지 않습니다.", exception.getMessage());
    }
}
