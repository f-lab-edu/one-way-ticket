package org.onewayticket.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.onewayticket.dto.PaymentConfirmRequestDto;
import org.onewayticket.dto.PaymentRequestDto;
import org.onewayticket.dto.PaymentResponseDto;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class TossPaymentController {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 클라이언트에서 "결제하기" 버튼 클릭하면 결제 금액 변경사항 확인을 위해
     * 결제 금액을 세션에 임시 저장함.
     */
    @PostMapping("/tempsave")
    public ResponseEntity<?> tempsave(HttpSession session, @RequestBody PaymentRequestDto paymentRequestDto) {
        session.setAttribute(paymentRequestDto.orderId(), paymentRequestDto.amount());
        return ResponseEntity.ok("결제 정보가 세션에 임시로 저장되었습니다.");
    }

    /**
     * 결제 승인 전, 결제 금액을 검증
     */
    @PostMapping("/verifyAmount")
    public ResponseEntity<?> verifyAmount(HttpSession session, @RequestBody PaymentRequestDto paymentRequestDto) {

        String amount = (String) session.getAttribute(paymentRequestDto.orderId());

        // 결제 요청 전 세션에 저장된 가격과 결제 승인 전 금액을 비교
        if (amount == null || !amount.equals(paymentRequestDto.amount()))
            return ResponseEntity.badRequest().build();

        // 검증에 사용했던 세션은 삭제
        session.removeAttribute(paymentRequestDto.orderId());

        return ResponseEntity.ok("결제 금액이 일치합니다.");
    }

    /**
     * 토스에게 결제 승인 요청하는 api
     * 클라이언트에게서 받은 결제 완료 요청에 시크릿 키를 더해 토스 서버로 보내는 요청
     */
    @PostMapping("/confirm")
    public ResponseEntity confirmPayment(@RequestBody PaymentConfirmRequestDto paymentConfirmRequestDto) throws Exception {

        /**
         * 토스 페이먼츠 API 요청시 필요한 비밀 키
         * 실제 secretKey로 교체 전 은닉 예정.
         */
        String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
        String authorizations = "Basic " + Base64.getEncoder().encodeToString((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));

        // 클라이언트에게서 받은 정보로 요청 데이터 생성
        Map<String, String> requestMap = Map.of(
                "orderId", paymentConfirmRequestDto.orderId(),
                "amount", paymentConfirmRequestDto.amount(),
                "paymentKey", paymentConfirmRequestDto.paymentKey()
        );

        String requestBody = objectMapper.writeValueAsString(requestMap);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorizations);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(objectMapper.writeValueAsString(requestMap), headers);

        String url = "https://api.tosspayments.com/v1/payments/confirm";
        /**
         * toss에 요청
         * 성공 시 : DB에 저장, 성공 메시지 반환.
         * 실패 시 : 실패 메시지 반환
         */
        try {
            ResponseEntity<PaymentResponseDto> response =
                    restTemplate.postForEntity(url, requestEntity, PaymentResponseDto.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                // TODO: DB에 결제 정보 저장
                return ResponseEntity.ok(response.getBody());
            } else {
                return ResponseEntity.status(response.getStatusCode()).body("결제에 실패하였습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("알 수 없는 오류 발생: " + e.getMessage());
        }

    }

    /**
     * 결제 취소 요청
     */
    @PostMapping("/cancel")
    public ResponseEntity<String> cancelPayment(String paymentKey, String cancelReason) {
        // 요청 URL
        String url = "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel";

        // Authorization 헤더 생성
        String secretKey = "test_sk_zXLkKEypNArWmo50nX3lmeaxYG5R"; // 실제 키로 교체 필요
        String authorization = "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        // 요청 데이터
        Map<String, String> requestBody = Map.of("cancelReason", cancelReason);

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorization);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HTTP 요청 본문 설정
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        // RestTemplate로 요청 전송
        return restTemplate.postForEntity(url, requestEntity, String.class);
    }

}
