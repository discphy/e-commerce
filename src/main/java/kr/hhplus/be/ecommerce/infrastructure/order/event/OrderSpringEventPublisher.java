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
    public void paid(OrderEvent.Paid event) {
        eventPublisher.publishEvent(event);
    }
}
