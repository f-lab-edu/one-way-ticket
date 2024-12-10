package org.onewayticket.controller;

import lombok.RequiredArgsConstructor;
import org.onewayticket.dto.MyPageDto;
import org.onewayticket.security.JwtUtil;
import org.onewayticket.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    @GetMapping("/info")
    public ResponseEntity<?> getMyPage(@RequestHeader("Authorization") String header) {
        String token = jwtUtil.extractTokenFromHeader(header);
        MyPageDto myPageDto = MyPageDto.from(memberService.getMyPageInfo(token));
        return ResponseEntity.ok(myPageDto);
    }

}
