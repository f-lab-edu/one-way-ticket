package org.onewayticket.kafka;

import com.onewayticket.payment.proto.FraudCheckedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onewayticket.dto.PaymentCreatedEvent;
import org.onewayticket.enums.PaymentStatus;
import org.onewayticket.service.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FraudDetectionConsumer {

    private final KafkaTemplate<String, FraudCheckedEvent> kafkaTemplate;
    private final PaymentService paymentService;

    @KafkaListener(topics = "payment-created-topic", groupId = "fraud-service")
    public void onPaymentCreated(PaymentCreatedEvent event) {
        log.info("[FraudConsumer] Received PaymentCreatedEvent, paymentId={}", event.paymentId());

        // 부정사용자 여부 판단
        boolean isFraud = checkFraudUser(event.paymentId());
        if (isFraud) {
            // Payment 상태를 FraudRejected로 업데이트
            paymentService.updatePaymentStatus(event.paymentId(), PaymentStatus.FRAUD_REJECTED);
            log.info("[FraudConsumer] Payment {} is FRAUD. Marking as FRAUD_REJECTED.", event.paymentId());
            FraudCheckedEvent fraudCheckedEvent = FraudCheckedEvent.newBuilder()
                    .setPaymentId(event.paymentId()).setIsFraud(true).build();
            kafkaTemplate.send("fraud-checked-topic", fraudCheckedEvent);

        } else {
            paymentService.updatePaymentStatus(event.paymentId(), PaymentStatus.FRAUD_PASSED);
            FraudCheckedEvent fraudCheckedEvent = FraudCheckedEvent.newBuilder()
                    .setPaymentId(event.paymentId()).setIsFraud(false).build();
            kafkaTemplate.send("fraud-checked-topic", fraudCheckedEvent);
        }
    }

    private boolean checkFraudUser(Long paymentId) {
        // TODO : fraud 검증 로직 추가 필요
        return paymentId % 2 == 0;
    }
}


