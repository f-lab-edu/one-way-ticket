package org.onewayticket.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserInfoDto(
        @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "아이디는 알파벳과 숫자로 이루어져야 합니다.")
        String username,

        @Size(min = 8, message = "비밀번호는 최소 8자리 이상이어야 합니다.")
        String password
) {}