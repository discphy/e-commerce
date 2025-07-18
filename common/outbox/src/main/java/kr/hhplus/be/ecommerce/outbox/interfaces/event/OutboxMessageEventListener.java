package kr.hhplus.be.ecommerce.outbox.interfaces.event;

import kr.hhplus.be.ecommerce.message.event.Event;
import kr.hhplus.be.ecommerce.message.event.EventType.GroupId;
import kr.hhplus.be.ecommerce.message.event.EventType.Topic;
import kr.hhplus.be.ecommerce.outbox.domain.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxMessageEventListener {

    private final OutboxService outboxService;

    @KafkaListener(topics = {
        Topic.COUPON_PUBLISH_REQUESTED,
        Topic.ORDER_COMPLETE_FAILED,
        Topic.ORDER_COMPLETED,
        Topic.ORDER_CREATED,
        Topic.PAYMENT_PAID,
        Topic.PAYMENT_FAILED,
        Topic.PAYMENT_CANCELED,
    }, groupId = GroupId.OUTBOX)
    public void handle(String message, Acknowledgment ack) {
        log.info("아웃 박스 이벤트 수신 {}", message);

        Event<?> event = Event.of(message, Object.class);
        outboxService.clearOutbox(event.getEventId());
        ack.acknowledge();
    }
}
