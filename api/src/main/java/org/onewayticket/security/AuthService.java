package org.onewayticket.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    public String generateToken(String referenceCode, String bookingEmail) {
        String token = TokenProvider.generateToken(referenceCode, bookingEmail);
        return token;
    }
}
