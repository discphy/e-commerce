package kr.hhplus.be.ecommerce.order.infrastructure;

import kr.hhplus.be.ecommerce.message.event.EventType;
import kr.hhplus.be.ecommerce.order.domain.OrderEvent;
import kr.hhplus.be.ecommerce.order.domain.OrderEventPublisher;
import kr.hhplus.be.ecommerce.outbox.OutboxEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCoreEventPublisher implements OrderEventPublisher {

    private final OutboxEventPublisher outboxEventPublisher;

    @Override
    public void created(OrderEvent.Created event) {
        outboxEventPublisher.publishEvent(EventType.ORDER_CREATED, event.getOrderId(), event);
    }

    @Override
    public void completed(OrderEvent.Completed event) {
        outboxEventPublisher.publishEvent(EventType.ORDER_COMPLETED, event.getOrderId(), event);
    }

    @Override
    public void completeFailed(OrderEvent.CompleteFailed event) {
        outboxEventPublisher.publishEvent(EventType.ORDER_COMPLETE_FAILED, event.getOrderId(), event);
    }
}
