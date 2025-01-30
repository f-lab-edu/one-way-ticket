package org.onewayticket.kafka;

import com.onewayticket.payment.proto.CouponUsedEvent;
import com.onewayticket.payment.proto.FraudCheckedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onewayticket.enums.PaymentStatus;
import org.onewayticket.service.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponConsumer {

    private final KafkaTemplate<String, CouponUsedEvent> kafkaTemplate;
    private final PaymentService paymentService;

    @KafkaListener(topics = "fraud-checked-topic", groupId = "coupon-service")
    public void onFraudChecked(FraudCheckedEvent event) {
        log.info("[CouponConsumer] Received FraudCheckedEvent, paymentId={}, isFraud={}",
                event.getPaymentId(), event.getIsFraud());

        if (event.getIsFraud()) {
            // 부정사용자이면 쿠폰 적용 로직 없음
            log.info("[CouponConsumer] Payment {} is FRAUD, no coupon process", event.getPaymentId());
            return;
        }

        // 실제 쿠폰 사용 로직
        applyCoupon(event.getPaymentId());

        // 결제 상태 업데이트
        paymentService.updatePaymentStatus(event.getPaymentId(), PaymentStatus.COUPON_APPLIED);
        log.info("[CouponConsumer] Coupon applied for Payment {}", event.getPaymentId());

        // 다음 단계로 넘어가기 위한 이벤트 발행
        CouponUsedEvent couponUsedEvent = CouponUsedEvent.newBuilder().setPaymentId(event.getPaymentId()).setDiscountAmount(0).setCouponUsed(true).build();
        kafkaTemplate.send("coupon-used-topic", couponUsedEvent);
    }

    private void applyCoupon(Long paymentId) {
        // TODO: 쿠폰 사용 로직 필요
    }
}
