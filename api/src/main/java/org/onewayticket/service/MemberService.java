package org.onewayticket.service;

import lombok.RequiredArgsConstructor;
import org.onewayticket.domain.Member;
import org.onewayticket.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Member getMyPageInfo(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
        return member;
    }

    public Member getMemberByUsername(String username) {
        return memberRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
    }
}
