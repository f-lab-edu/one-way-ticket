package org.onewayticket.controller;


import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.onewayticket.domain.TossPayment;
import org.onewayticket.dto.PaymentConfirmRequestDto;
import org.onewayticket.dto.PaymentRequestDto;
import org.onewayticket.dto.PaymentResponseDto;
import org.onewayticket.service.PaymentService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class TossPaymentController {

    private final PaymentService paymentService;

    /**
     * 클라이언트에서 "결제하기" 버튼 클릭하면 결제 금액 변경사항 확인을 위해
     * 결제 금액을 세션에 임시 저장함.
     */
    @PostMapping("/tempsave")
    public ResponseEntity<?> tempsave(HttpSession session, @Valid @RequestBody PaymentRequestDto paymentRequestDto) {
        paymentService.tempSaveAmount(session, paymentRequestDto);
        return ResponseEntity.ok("결제 정보가 세션에 저장되었습니다.");
    }

    /**
     * 결제 승인 전, 결제 금액을 검증
     */
    @PostMapping("/verifyAmount")
    public ResponseEntity<?> verifyAmount(HttpSession session, @Valid @RequestBody PaymentRequestDto paymentRequestDto) {
        paymentService.verifyAmount(session, paymentRequestDto);
        return ResponseEntity.ok("결제 금액이 일치합니다.");
    }

    /**
     * 토스에게 결제 승인 요청하는 api
     * 클라이언트에게서 받은 결제 완료 요청에 시크릿 키를 더해 토스 서버로 보내는 요청
     */
    @PostMapping("/confirm")
    public ResponseEntity<PaymentResponseDto> confirmPayment(@Valid @RequestBody PaymentConfirmRequestDto paymentConfirmRequestDto) {
        TossPayment tossPayment = paymentService.confirmPayment(paymentConfirmRequestDto);
        paymentService.savePayment(tossPayment);
        return ResponseEntity.ok(PaymentResponseDto.from(tossPayment));
    }

    /**
     * 결제 취소 요청
     */
    @PostMapping("/cancel")
    public ResponseEntity<?> cancelPayment(@RequestParam @NotNull String paymentKey, @RequestParam @NotNull String cancelReason) {
           // 결제 취소 요청
           TossPayment tossPayment = paymentService.cancelPayment(paymentKey, cancelReason);
           // DB 상태 업데이트
           TossPayment updatedPayment = paymentService.updatePaymentStatus(tossPayment);
           return ResponseEntity.ok(PaymentResponseDto.from(updatedPayment));

    }


}
