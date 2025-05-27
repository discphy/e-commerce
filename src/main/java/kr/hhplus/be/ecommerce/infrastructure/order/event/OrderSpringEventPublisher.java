package kr.hhplus.be.ecommerce.infrastructure.order.event;

import kr.hhplus.be.ecommerce.domain.order.OrderEvent;
import kr.hhplus.be.ecommerce.domain.order.OrderEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderSpringEventPublisher implements OrderEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void created(OrderEvent.Created event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public void completed(OrderEvent.Completed event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public void completeFailed(OrderEvent.CompleteFailed event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public void paymentWaited(OrderEvent.PaymentWaited event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public void failed(OrderEvent.Failed event) {
        eventPublisher.publishEvent(event);
    }
}
