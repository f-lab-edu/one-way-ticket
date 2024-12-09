package org.onewayticket.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {
    private static final String SECRET_KEY = "MY-SECRET-KEY";

    public static String generateToken(String username, long expirationMillis) {
        long nowMillis = System.currentTimeMillis();
        long expMillis = nowMillis + expirationMillis;

        String header = Base64.getUrlEncoder().encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes());
        String payload = Base64.getUrlEncoder().encodeToString(("{\"sub\":\"" + username + "\",\"exp\":" + expMillis + "}").getBytes());

        String signature = createSignature(header + "." + payload);
        return header + "." + payload + "." + signature;
    }

    public static boolean validateToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;

            String headerPayload = parts[0] + "." + parts[1];
            String signature = createSignature(headerPayload);

            if (!signature.equals(parts[2])) return false;
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            Map<String, String> payloadMap = parsePayload(payload);
            long exp = Long.parseLong(payloadMap.get("exp"));
            return System.currentTimeMillis() < exp;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getUsername(String token) {
        try {
            String[] parts = token.split("\\.");
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            Map<String, String> payloadMap = parsePayload(payload);
            return payloadMap.get("sub"); //
        } catch (Exception e) {
            throw new RuntimeException("유효하지 않은 토큰입니다.", e);
        }
    }

    private static String createSignature(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
            mac.init(secretKey);
            byte[] signatureBytes = mac.doFinal(data.getBytes());
            return Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes);
        } catch (Exception e) {
            throw new RuntimeException("토큰 시그니처 생성 중 오류가 발생했습니다.", e);
        }
    }

    private static Map<String, String> parsePayload(String payload) {
        Map<String, String> map = new HashMap<>();
        String[] pairs = payload.replace("{", "").replace("}", "").split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            map.put(keyValue[0].replace("\"", ""), (keyValue[1]).replace("\"", ""));
        }
        return map;
    }
}
