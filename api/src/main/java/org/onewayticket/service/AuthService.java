package org.onewayticket.service;

import lombok.RequiredArgsConstructor;
import org.onewayticket.domain.Member;
import org.onewayticket.repository.MemberRepository;
import org.onewayticket.security.JwtUtil;
import org.onewayticket.security.PasswordUtil;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    public Member register(String username, String password) {
        if (!username.matches("^[a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException("아이디 형식을 확인해주세요.");
        }

        String hashedPassword = PasswordUtil.hashPassword(password);
        Member member = Member.builder()
                .username(username)
                .password(hashedPassword)
                .build();

        return memberRepository.save(member);
    }


    public String authenticate(String username, String password) {
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다."));

        if (PasswordUtil.verifyPassword(password, member.getPassword())) {
            // 비밀번호가 일치하면 토큰 생성
            return jwtUtil.generateToken(member.getUsername(), 24 * 60 * 60 * 1000);
        }
        throw new IllegalArgumentException("아이디 비밀번호가 올바르지 않습니다.");
    }


}
