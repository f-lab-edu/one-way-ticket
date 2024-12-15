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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("사용자는 마이페이지를 통해 자신의 정보를 확인할 수 있습니다.")
    void GetMyPageInfo_Success() {
        // Given
        String username = "testUser";
        Mockito.when(memberRepository.findByUsername(username))
                .thenReturn(Optional.of(new Member(1L, "testUser", "password")));

        // When
        Member member = memberService.getMyPageInfo(username);

        // Then
        assertNotNull(member);
        assertEquals("testUser", member.getUsername());
    }

    @Test
    @DisplayName("이름을 통해 멤버 객체를 불러올 수 있습니다.")
    void GetMemberObject() {
        // Given
        String username = "testUser";
        Mockito.when(memberRepository.findByUsername(username))
                .thenReturn(Optional.of(new Member(1L, "testUser", "password")));

        // When
        Member member = memberService.getMemberByUsername(username);

        // Then
        assertNotNull(member);
        assertEquals("testUser", member.getUsername());
    }
}
