package org.onewayticket.service;

import org.junit.jupiter.api.BeforeEach;
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

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private MemberService memberService;

    private String validToken;

    @BeforeEach
    void setUp() {
        validToken = jwtUtil.generateToken("testUser", 3600000); // 1시간 유효한 토큰
    }

    @Test
    @DisplayName("유효한 토큰으로 사용자를 정상 조회")
    void GetMyPageInfo_Success() {
        // Given
        Mockito.when(jwtUtil.getUsername(validToken)).thenReturn("testUser");
        Mockito.when(memberRepository.findByUsername("testUser"))
                .thenReturn(Optional.of(new Member(1L, "testUser", "password")));

        // When
        Member member = memberService.getMyPageInfo(validToken);

        // Then
        assertNotNull(member);
        assertEquals("testUser", member.getUsername());
    }

    @Test
    @DisplayName("토큰으로 조회 시 사용자 없음 예외 발생")
    void GetMyPageInfo_UserNotFound() {
        // Given
        Mockito.when(jwtUtil.getUsername(validToken)).thenReturn("testUser");
        Mockito.when(memberRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        // When & Then
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> memberService.getMyPageInfo(validToken));
        assertTrue(exception.getMessage().contains("사용자를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("회원 ID로 사용자 조회 성공")
    void GetMemberById_Success() {
        // Given
        Long memberId = 1L;
        Member member = new Member(memberId, "testUser", "password");

        Mockito.when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // When
        Member result = memberService.getMemberById(memberId);

        // Then
        assertNotNull(result);
        assertEquals(memberId, result.getId());
    }

    @Test
    @DisplayName("회원 ID로 사용자 조회 실패")
    void GetMemberById_UserNotFound() {
        // Given
        Long memberId = 1L;

        Mockito.when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> memberService.getMemberById(memberId));
        assertTrue(exception.getMessage().contains("사용자를 찾을 수 없습니다."));
    }
}
