package kr.hhplus.be.ecommerce.payment.infrastructure;

import kr.hhplus.be.ecommerce.message.event.EventType;
import kr.hhplus.be.ecommerce.outbox.OutboxEventPublisher;
import kr.hhplus.be.ecommerce.payment.domain.PaymentEvent;
import kr.hhplus.be.ecommerce.payment.domain.PaymentEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCoreEventPublisher implements PaymentEventPublisher {

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
