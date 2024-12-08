package org.onewayticket.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.onewayticket.domain.TossPayment;
import org.onewayticket.dto.PaymentConfirmRequestDto;
import org.onewayticket.dto.PaymentRequestDto;
import org.onewayticket.enums.PaymentStatus;
import org.onewayticket.repository.PaymentRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private static final String CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";
    private static final String CANCEL_URL = "https://api.tosspayments.com/v1/payments/";
    private static final String SECRET_KEY = "test_sk_zXLkKEypNArWmo50nX3lmeaxYG5R"; // 실제 키로 교체 필요

    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public void tempSaveAmount(HttpSession session, PaymentRequestDto paymentRequestDto) {
        if (session == null) {
            throw new IllegalStateException("세션이 만료되어 결제값을 저장할 수 없습니다.");
        }
        session.setAttribute(paymentRequestDto.orderId(), paymentRequestDto.amount());
    }

    public void verifyAmount(HttpSession session, PaymentRequestDto paymentRequestDto) {
        String amount = (String) session.getAttribute(paymentRequestDto.orderId());
        if (amount == null || !amount.equals(paymentRequestDto.amount())) {
            throw new IllegalArgumentException("결제 금액이 일치하지 않습니다.");
        }
    }

    public TossPayment confirmPayment(PaymentConfirmRequestDto paymentConfirmRequestDto) {
        String authorizations = "Basic " + Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8));

        Map<String, Object> requestMap = Map.of(
                "orderId", paymentConfirmRequestDto.orderId(),
                "amount", paymentConfirmRequestDto.amount(),
                "paymentKey", paymentConfirmRequestDto.paymentKey()
        );

        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(requestMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorizations);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<TossPayment> response = restTemplate.postForEntity(CONFIRM_URL, requestEntity, TossPayment.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalArgumentException("결제 승인 실패: " + response.getStatusCode());
        }
        TossPayment tossPayment = response.getBody();
        if (tossPayment == null) {
            throw new IllegalArgumentException("TossPayment 객체가 null입니다.");
        }

        return tossPayment;

    }

    public void savePayment(TossPayment tossPayment) {
        paymentRepository.save(tossPayment);
    }

    /**
     * 결제 취소 요청을 Toss API로 보냄
     */
    public TossPayment cancelPayment(String paymentKey, String cancelReason) {
        String authorization = "Basic " + Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8));

        // 요청 URL
        String url = CANCEL_URL + paymentKey + "/cancel";

        // 요청 데이터
        Map<String, String> requestBody = Map.of("cancelReason", cancelReason);

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorization);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HTTP 요청 본문 설정
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<TossPayment> response = restTemplate.postForEntity(url, requestEntity, TossPayment.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalArgumentException("결제 취소 실패: " + response.getStatusCode());
        }

        TossPayment tossPayment = response.getBody();

        if (tossPayment == null) {
            throw new IllegalStateException("TossPayment 객체가 null입니다.");
        }

        return tossPayment;
    }

    /**
     * DB에서 결제 상태를 업데이트
     */
    public TossPayment updatePaymentStatus(TossPayment tossPayment) {
        if (tossPayment.getPaymentStatus() == PaymentStatus.CANCELED) {
            throw new IllegalStateException("이미 취소된 결제입니다.");
        }
        try {
            tossPayment.updatePaymentStatus(PaymentStatus.CANCELED);
            return paymentRepository.save(tossPayment);
        } catch (DataAccessException e) {
            throw new DataIntegrityViolationException("DB 단에서 데이터 수정 중 오류가 발생하였습니다.", e);
        }
    }

}
