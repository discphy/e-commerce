package kr.hhplus.be.ecommerce.interfaces.payment.event;

import kr.hhplus.be.ecommerce.domain.order.OrderEvent;
import kr.hhplus.be.ecommerce.domain.payment.PaymentCommand;
import kr.hhplus.be.ecommerce.domain.payment.PaymentService;
import kr.hhplus.be.ecommerce.support.event.Event;
import kr.hhplus.be.ecommerce.support.event.EventType.GroupId;
import kr.hhplus.be.ecommerce.support.event.EventType.Topic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentOrderMessageEventListener {

    private final PaymentService paymentService;

    @KafkaListener(topics = Topic.ORDER_CREATED, groupId = GroupId.PAYMENT)
    public void handleOrderCreated(String message, Acknowledgment ack) {
        log.info("주문 생성 이벤트 수신 {}", message);

        Event<OrderEvent.Created> event = Event.of(message, OrderEvent.Created.class);
        OrderEvent.Created payload = event.getPayload();

        paymentService.payPayment(
            PaymentCommand.Payment.of(
                payload.getOrderId(),
                payload.getUserId(),
                payload.getUserCouponId(),
                payload.getTotalPrice()
            )
        );
        ack.acknowledge();
    }

    @KafkaListener(topics = Topic.ORDER_COMPLETE_FAILED, groupId = GroupId.PAYMENT)
    public void handleOrderCompleteFailed(String message, Acknowledgment ack) {
        log.info("주문 완료 실패 이벤트 수신 {}", message);

        Event<OrderEvent.CompleteFailed> event = Event.of(message, OrderEvent.CompleteFailed.class);
        OrderEvent.CompleteFailed payload = event.getPayload();

        paymentService.cancelPayment(payload.getOrderId());
        ack.acknowledge();
    }
}
