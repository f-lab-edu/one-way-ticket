package org.onewayticket.kafka;

import com.onewayticket.payment.proto.PointUsedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onewayticket.enums.PaymentStatus;
import org.onewayticket.service.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentCompletionConsumer {

    private final PaymentService paymentService;

    @KafkaListener(topics = "point-used-topic", groupId = "payment-completion")
    public void onPointUsed(PointUsedEvent event) {
        log.info("[PaymentCompletionConsumer] Received PointUsedEvent, paymentId={}, pointUsed={}",
                event.getPaymentId(), event.getPointUsed());

        // 모든 절차가 끝났다고 가정 → 결제 완료
        paymentService.updatePaymentStatus(event.getPaymentId(), PaymentStatus.COMPLETED);
        log.info("[PaymentCompletionConsumer] Payment {} is COMPLETED.", event.getPaymentId());
    }
}
