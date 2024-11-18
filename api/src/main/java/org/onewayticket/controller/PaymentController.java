package org.onewayticket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PaymentController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/confirm")
    public ResponseEntity<Map<String, Object>> confirmPayment(@RequestBody String jsonBody) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String orderId;
        String amount;
        String paymentKey;

        try {
            // 클라이언트에서 받은 JSON 요청 바디를 파싱합니다.
            Map<String, Object> requestData = objectMapper.readValue(jsonBody, Map.class);
            paymentKey = (String) requestData.get("paymentKey");
            orderId = (String) requestData.get("orderId");
            amount = (String) requestData.get("amount");
        } catch (Exception e) {
            throw new RuntimeException("JSON 파싱 에러", e);
        }

        // 요청 JSON 객체 생성
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("orderId", orderId);
        requestMap.put("amount", amount);
        requestMap.put("paymentKey", paymentKey);

        // 토스페이먼츠 API 인증 헤더 생성
        String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        // HTTP 연결 설정
        URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        // 요청 데이터를 전송합니다.
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(objectMapper.writeValueAsString(requestMap).getBytes(StandardCharsets.UTF_8));

        int code = connection.getResponseCode();
        boolean isSuccess = code == 200;

        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();

        // 응답 데이터를 처리합니다.
        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        Map<String, Object> responseMap = objectMapper.readValue(reader, Map.class);
        responseStream.close();

        return ResponseEntity.status(code).body(responseMap);
    }
}
