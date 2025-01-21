package org.onewayticket.kafka;

import com.onewayticket.payment.proto.CouponUsedEvent;
import com.onewayticket.payment.proto.PointUsedEvent;
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
public class PointConsumer {

    private final KafkaTemplate<String, PointUsedEvent> kafkaTemplate;
    private final PaymentService paymentService;

    @KafkaListener(topics = "coupon-used-topic", groupId = "point-service")
    public void onCouponUsed(CouponUsedEvent event) {
        log.info("[PointConsumer] Received CouponUsedEvent, paymentId={}, discountAmount={}",
                event.getPaymentId(), event.getDiscountAmount());

        // 포인트 차감 로직
        usePoint(event.getPaymentId());

        // 결제 상태 업데이트
        paymentService.updatePaymentStatus(event.getPaymentId(), PaymentStatus.POINT_APPLIED);
        log.info("[PointConsumer] Point used for Payment {}", event.getPaymentId());

        PointUsedEvent pointUsedEvent = PointUsedEvent.newBuilder().setPaymentId(event.getPaymentId()).setDiscountAmount(0).setPointUsed(true).build();
        kafkaTemplate.send("point-used-topic", pointUsedEvent);
    }

    private void usePoint(Long paymentId) {
        // TODO: 포인트 사용 로직 필요
    }
}
