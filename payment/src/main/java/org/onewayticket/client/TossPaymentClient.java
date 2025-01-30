package org.onewayticket.client;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class TossPaymentClient {

    private final RestTemplate restTemplate;
    private final TossPaymentProperties properties; // Toss 관련 설정값 (Base URL, Secret Key 등)

//    /**
//     * 결제 승인 요청
//     */
//    public TossPayment confirmPayment(TossPaymentConfirmRequestDto requestDto) {
//        String url = properties.getBaseUrl() + "/v1/payments/confirm";
//
//        HttpHeaders headers = createHeaders(properties.getSecretKey());
//        HttpEntity<TossPaymentConfirmRequestDto> entity = new HttpEntity<>(requestDto, headers);
//
//        try {
//            ResponseEntity<TossPayment> response = restTemplate.postForEntity(url, entity, TossPayment.class);
//            return response.getBody();
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to confirm payment with Toss", e);
//        }
//    }


}
