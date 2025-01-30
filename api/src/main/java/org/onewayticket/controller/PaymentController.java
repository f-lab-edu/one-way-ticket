package org.onewayticket.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.onewayticket.domain.Payment;
import org.onewayticket.dto.PaymentRequestDto;
import org.onewayticket.dto.PaymentStatusResponseDto;
import org.onewayticket.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    // 결제 요청 API
    @PostMapping
    public ResponseEntity<?> requestPayment(@RequestBody PaymentRequestDto requestDto,
                                            HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        paymentService.createPayment(username, requestDto.orderId(), requestDto.amount());
        return ResponseEntity.ok("결제 요청이 처리되었습니다.");
    }

    // 결제 상태 확인 API
    @GetMapping("/{paymentId}")
    public ResponseEntity<?> getPaymentStatus(@PathVariable Long paymentId) {
        Payment payment = paymentService.getPaymentStatus(paymentId);
        return ResponseEntity.ok(PaymentStatusResponseDto.from(payment));
    }
}
