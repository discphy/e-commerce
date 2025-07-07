package kr.hhplus.be.ecommerce.coupon.infrastructure;

import kr.hhplus.be.ecommerce.coupon.domain.CouponEvent;
import kr.hhplus.be.ecommerce.coupon.domain.CouponEventPublisher;
import kr.hhplus.be.ecommerce.message.DefaultMessage;
import kr.hhplus.be.ecommerce.message.Message;
import kr.hhplus.be.ecommerce.message.MessageProducer;
import kr.hhplus.be.ecommerce.message.event.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponCoreEventPublisher implements CouponEventPublisher {

    private final ApplicationEventPublisher eventPublisher;
    private final MessageProducer messageProducer;

    @Override
    public void publishRequested(CouponEvent.PublishRequested event) {
        Message message = DefaultMessage.of(EventType.COUPON_PUBLISH_REQUESTED, event.getCouponId(), event);
        messageProducer.send(message);
    }

    @Override
    public void published(CouponEvent.Published event) {
        eventPublisher.publishEvent(event);
    }
}
