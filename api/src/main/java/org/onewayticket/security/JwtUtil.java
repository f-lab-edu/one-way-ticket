package org.onewayticket.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private static final String SECRET_KEY = "MY-SECRET-KEY";
    public final ObjectMapper objectMapper;

    public String generateToken(String username, long expirationMillis) {
        long nowMillis = System.currentTimeMillis();
        long expMillis = nowMillis + expirationMillis;

        String header = Base64.getUrlEncoder().encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes());
        String payload = Base64.getUrlEncoder().encodeToString(("{\"sub\":\"" + username + "\",\"exp\":" + expMillis + "}").getBytes());

        String signature = createSignature(header + "." + payload, SECRET_KEY);
        return header + "." + payload + "." + signature;
    }

    public boolean validateToken(String token) {
        try {
            String[] parts = splitToken(token);
            String headerPayload = parts[0] + "." + parts[1];
            String signature = createSignature(headerPayload, SECRET_KEY);

            if (!signature.equals(parts[2])) {
                return false;
            }

            Map<String, Object> payloadMap = parsePayload(parts[1]);
            long exp = ((Number) payloadMap.get("exp")).longValue();
            return System.currentTimeMillis() < exp;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("유효한 토큰이 필요합니다.");
        }
        return authorizationHeader.substring(7);
    }

    public String getUsername(String token) {
        try {
            String[] parts = splitToken(token);
            Map<String, Object> payloadMap = parsePayload(parts[1]);
            return (String) payloadMap.get("sub");
        } catch (Exception e) {
            throw new RuntimeException("유효하지 않은 토큰입니다.", e);
        }
    }

    private String[] splitToken(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("JWT 구조가 올바르지 않습니다.");
        }
        return parts;
    }

    private Map<String, Object> parsePayload(String payload) {
        try {
            String decodedPayload = new String(Base64.getUrlDecoder().decode(payload));
            return objectMapper.readValue(decodedPayload, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("페이로드 파싱 실패", e);
        }
    }

    private String createSignature(String data, String secretKey) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] signatureBytes = mac.doFinal(data.getBytes());
            return Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes);
        } catch (Exception e) {
            throw new RuntimeException("HMAC SHA256 서명 생성 실패", e);
        }
    }
}