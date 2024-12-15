package org.onewayticket.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.onewayticket.dto.MyPageDto;
import org.onewayticket.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/info")
    public ResponseEntity<?> getMyPage(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        MyPageDto myPageDto = MyPageDto.from(memberService.getMyPageInfo(username));
        return ResponseEntity.ok(myPageDto);
    }

}
