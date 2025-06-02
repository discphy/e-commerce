package kr.hhplus.be.ecommerce.infrastructure.payment.event;

import kr.hhplus.be.ecommerce.domain.payment.PaymentEvent;
import kr.hhplus.be.ecommerce.domain.payment.PaymentEventPublisher;
import kr.hhplus.be.ecommerce.support.event.EventType;
import kr.hhplus.be.ecommerce.support.outbox.OutboxEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventPublisherImpl implements PaymentEventPublisher {

    private final OutboxEventPublisher outboxEventPublisher;

    @Override
    public void paid(PaymentEvent.Paid event) {
        outboxEventPublisher.publishEvent(EventType.PAYMENT_PAID, event.getOrderId(), event);
    }

    @Override
    public void payFailed(PaymentEvent.PayFailed event) {
        outboxEventPublisher.publishEvent(EventType.PAYMENT_FAILED, event.getOrderId(), event);
    }

    @Override
    public void canceled(PaymentEvent.Canceled event) {
        outboxEventPublisher.publishEvent(EventType.PAYMENT_CANCELED, event.getOrderId(), event);
    }
}
