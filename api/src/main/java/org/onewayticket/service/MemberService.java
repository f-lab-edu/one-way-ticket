package org.onewayticket.service;

import lombok.RequiredArgsConstructor;
import org.onewayticket.domain.Member;
import org.onewayticket.repository.MemberRepository;
import org.onewayticket.security.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MemberService {
    public final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    public Member getMyPageInfo(String token) {
        String username = jwtUtil.getUsername(token);
        System.out.println("username = " + username);
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
        System.out.println("member = " + member);
        return member;
    }

    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
    }

    public Member getMemberByUsername(String username) {
        return memberRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
    }
}
