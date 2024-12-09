package org.onewayticket.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.onewayticket.dto.UserInfoDto;
import org.onewayticket.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserInfoDto userInfoDto) {
        memberService.register(userInfoDto.username(), userInfoDto.password());
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserInfoDto userInfoDto) {
        Map<String, String> response = memberService.authenticate(userInfoDto.username(), userInfoDto.password());
        return ResponseEntity.ok(response);
    }

}
