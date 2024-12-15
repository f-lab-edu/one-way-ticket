package org.onewayticket.dto;

import org.onewayticket.domain.Member;

public record MyPageDto(
        String username
) {
    public static MyPageDto from(Member member){
        return new MyPageDto(member.getUsername());
    }
}
