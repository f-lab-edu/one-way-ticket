package org.onewayticket.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onewayticket.dto.PaymentCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentCreatedEventHandler {

    private final KafkaTemplate<String, PaymentCreatedEvent> kafkaTemplate;

    /**
     * PaymentService.createPayment()가 트랜잭션 커밋된 후,
     * Spring 이벤트를 받아 Kafka로 결제생성 이벤트 발행.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentCreated(PaymentCreatedEvent event) {
        log.info("Handling PaymentCreatedEvent, paymentId={}", event.paymentId());
        // 카프카에 "payment-created" 이벤트 발행
        kafkaTemplate.send("payment-created-topic", event);
    }
}
