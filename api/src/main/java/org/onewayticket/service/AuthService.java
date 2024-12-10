package org.onewayticket.service;

import lombok.RequiredArgsConstructor;
import org.onewayticket.domain.Member;
import org.onewayticket.repository.MemberRepository;
import org.onewayticket.security.JwtUtil;
import org.onewayticket.security.PasswordUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
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

        if (password.length() < 8) {
            throw new IllegalArgumentException("비밀번호가 너무 짧습니다..");
        }

        Member member = Member.builder()
                .username(username)
                .password(PasswordUtil.hashPassword(password))
                .build();

        return memberRepository.save(member);
    }


    public Map<String, String> authenticate(String username, String password) {
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다."));
        if (member != null && member.getPassword().equals(PasswordUtil.hashPassword(password))) {
            String token = jwtUtil.generateToken(member.getUsername(), 24 * 60 * 60 * 1000);
            return new HashMap<>() {{
                put("token", token);
            }};
        }
        throw new IllegalArgumentException("아이디 비밀번호가 올바르지 않습니다.");
    }


}
