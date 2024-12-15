package org.onewayticket.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.onewayticket.dto.UserInfoDto;
import org.onewayticket.service.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody UserInfoDto userInfoDto) {
        authService.register(userInfoDto.username(), userInfoDto.password());
        String token = authService.authenticate(userInfoDto.username(), userInfoDto.password());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        return ResponseEntity.ok().headers(headers).build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody UserInfoDto userInfoDto) {
        String token = authService.authenticate(userInfoDto.username(), userInfoDto.password());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        return ResponseEntity.ok().headers(headers).build();
    }
}
